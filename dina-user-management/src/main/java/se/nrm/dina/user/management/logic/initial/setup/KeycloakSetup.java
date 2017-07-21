/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.initial.setup;

//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
//import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.core.Response; 
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder; 
import org.keycloak.admin.client.resource.ClientsResource; 
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation; 
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.utils.AccountStatus;
import se.nrm.dina.user.management.utils.CommonString; 
import se.nrm.dina.user.management.utils.Util;

/**
 *
 * @author idali
 */
@Startup                            // initialize ejb at deployment time
@Singleton
public class KeycloakSetup implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    private static final String MASTER_REALM = "master";
//    private static final String ADMIN_CLIENT_ID = "admin-cli";
//    private static final String KEYCLOAK_URL = "http://localhost:8080/auth";
    
    private static final String REGEX = ".*/(.*)$";
    private static final String PURPOSE = "Super admin";

//    private static final String MASTER_ADMIN_USERNAME = "admin";
//    private static final String MASTER_ADMIN_PASSWORD = "dina";

    private String superUsername;
    private String superPassword;
    
    private String keycloakAuthURL;
    private final String realmName = "myrealm";
    private String mailHost;
    private String mailPort;
    private String mailFrom;
    private String emailUserName;
    private String emailPassword;
    
    private Keycloak keycloakClient; 

    public KeycloakSetup() {
    }

    @PostConstruct
    public void init() {
        logger.info("init");
        
        uploadProperties();  
        buildRealm();
        
        RealmsResource realms = keycloakClient.realms();
        boolean isRealmExist = realms.findAll().stream() 
                                     .anyMatch(r -> r.getDisplayName().equals(realmName)); 
        if(!isRealmExist) { 
            createRealm();
            
            Map<String, String> roleMap = new HashMap();
            roleMap.put(CommonString.getInstance().getAdminRole(), CommonString.getInstance().getAdminRoleDescription());
            roleMap.put(CommonString.getInstance().getUserRole(), CommonString.getInstance().getUserRoleDescription());
            roleMap.put(CommonString.getInstance().getDisabledUserRole(), CommonString.getInstance().getUserRoleDescription());
            
            roleMap.entrySet().stream()
                    .forEach(r -> {
                        createRealmRoles(r.getKey(), r.getValue());
                    });
              
            createEndpointClient();
            createUserManagementClient();

            roleMap.entrySet().stream()
                    .forEach(r -> {
                        createClientRole(CommonString.getInstance().getDinaRestClientId(), r.getKey(), r.getValue());
                        createClientRole(CommonString.getInstance().getUserManagementClientId(), r.getKey(), r.getValue());
                    }); 
            createInitialUser(); 
        }    
    }

    private void createInitialUser() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(superUsername);
        user.setEmail(superUsername);
        user.setFirstName(CommonString.getInstance().getSuperUserFirstName());
        user.setLastName(CommonString.getInstance().getSuperUserLastName()); 
        user.setEnabled(Boolean.TRUE);
        user.setEmailVerified(Boolean.TRUE);
         
        Map<String, List<String>> attributes = new HashMap<>();
        List<String> purposeList = new ArrayList<>();
        purposeList.add(PURPOSE);
        attributes.put(CommonString.getInstance().getPurpose(), purposeList);
        
        List<String> statusList = new ArrayList<>();
        statusList.add(AccountStatus.Existing.getText());
        attributes.put(CommonString.getInstance().getStatus(), statusList);  
        user.setAttributes(attributes);    
          
        Response response = keycloakClient.realm(realmName).users().create(user); 
        String locationHeader = response.getHeaderString(CommonString.getInstance().getLocation());
        response.close();

        if (locationHeader != null) {
            UserResource userResource = keycloakClient.realm(realmName).users().get(locationHeader.replaceAll(REGEX, "$1"));  
             
            resetPassword(userResource);
            setReamlRole(userResource);
            setClientRole(CommonString.getInstance().getDinaRestClientId(), userResource);
            setClientRole(CommonString.getInstance().getUserManagementClientId(), userResource);
        }
        keycloakClient.close();
    }
     
    private void setClientRole( String clientId, UserResource userResource) {
 
        ClientsResource clientsResource = keycloakClient.realm(realmName).clients();
        List<ClientRepresentation> crs = clientsResource.findAll();
        ClientRepresentation cr = crs.stream()
                                    .filter(c -> c.getClientId().equals(clientId))
                                    .findFirst().get();
        
        String cId = cr.getId();  
        List<RoleRepresentation> newRole = new ArrayList<>();
        List<RoleRepresentation> clrs = clientsResource.get(cId).roles().list();
        clrs.stream()
                .forEach(rr -> {
                    if (rr.getName().equals(CommonString.getInstance().getAdminRole())) {
                        newRole.add(rr); 
                    }
                });
        userResource.roles().clientLevel(cId).add(newRole);
    } 
    
    private void setReamlRole(UserResource userResource) {
        List<RoleRepresentation> rrs = userResource.roles().realmLevel().listAll();
        userResource.roles().realmLevel().remove(rrs);
   
        List<RoleRepresentation> dinaRealmRoles = keycloakClient.realm(realmName).roles().list();

        List<RoleRepresentation> newRole = new ArrayList<>();
        dinaRealmRoles.stream()
                .filter(drr -> !drr.getName().equals(CommonString.getInstance().getOfflineAccessRole()))
                .filter(drr -> !drr.getName().equals(CommonString.getInstance().getUmaAuthorizationRole()))
                        .forEach(drr -> { 
                            if(drr.getName().equals(CommonString.getInstance().getAdminRole())) {
                                newRole.add(drr);
                            } 
                        });  
        userResource.roles().realmLevel().add(newRole); 
    }
    
    
    private void resetPassword(UserResource userResource) {

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(superPassword);
        cred.setTemporary(false);

        userResource.resetPassword(cred);
    }

    private void createClientRole(String clientId, String role, String description) { 
        RoleRepresentation clientRoleRepresentation = new RoleRepresentation();
        clientRoleRepresentation.setName(role);
        clientRoleRepresentation.setDescription(description);
        
        clientRoleRepresentation.setClientRole(true);
        keycloakClient.realm(realmName).clients().findByClientId(clientId).forEach(clientRepresentation ->
            keycloakClient.realm(realmName).clients().get(clientRepresentation.getId()).roles().create(clientRoleRepresentation)
        ); 
    }
    
    private void createRealmRoles(String role, String roleDescription) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role); 
        roleRepresentation.setDescription(roleDescription);
        keycloakClient.realm(realmName).roles().create(roleRepresentation); 
    }
    
    private void createUserManagementClient() {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(CommonString.getInstance().getUserManagementClientId());
        clientRepresentation.setName(CommonString.getInstance().getUserManagementClientName());
        clientRepresentation.setDescription("user management to management user account in keycloak");
        clientRepresentation.setEnabled(Boolean.TRUE);
        clientRepresentation.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
        clientRepresentation.setDirectAccessGrantsEnabled(Boolean.FALSE); 
        clientRepresentation.setBearerOnly(Boolean.TRUE);
         
        keycloakClient.realm(realmName).clients().create(clientRepresentation);  
    }
    
    private void createEndpointClient() { 
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(CommonString.getInstance().getDinaRestClientId());
        clientRepresentation.setName(CommonString.getInstance().getDinaRestClientName());
        clientRepresentation.setDescription("dina rest endpoint to retrieve token");
        clientRepresentation.setEnabled(Boolean.TRUE);
        clientRepresentation.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
        clientRepresentation.setDirectAccessGrantsEnabled(Boolean.TRUE);
        
        List<ProtocolMapperRepresentation> protocolMappers = new ArrayList();  
          
        ProtocolMapperRepresentation protocolMapper = new ProtocolMapperRepresentation();
        protocolMapper.setName(CommonString.getInstance().getRealmRoleClaimName());
        protocolMapper.setConsentText(CommonString.getInstance().getProtocolMapperConsentTextRealmRole());
        protocolMapper.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
        protocolMapper.setConsentRequired(true); 
        protocolMapper.setProtocolMapper(CommonString.getInstance().getProtocolMapperRealmRoleMapper()); 
        protocolMapper.setConfig(Util.getInstance().buildProtocolMap(CommonString.getInstance().getRealmRoleClaimName(),
                                                                        CommonString.getInstance().getRealmRoleClaimName())); 
        protocolMappers.add(protocolMapper); 
         
 
        protocolMapper = new ProtocolMapperRepresentation();
        protocolMapper.setName(CommonString.getInstance().getEmail());
        protocolMapper.setConsentText(CommonString.getInstance().getProtocolMapperConsentTextEmail());
        protocolMapper.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
        protocolMapper.setConsentRequired(true); 
        protocolMapper.setProtocolMapper(CommonString.getInstance().getProtocolMapperPropertyMapper()); 
        protocolMapper.setConfig(Util.getInstance().buildProtocolMap(CommonString.getInstance().getEmail(),
                                                                        CommonString.getInstance().getEmail())); 
        protocolMappers.add(protocolMapper); 
         
        protocolMapper = new ProtocolMapperRepresentation();
        protocolMapper.setName(CommonString.getInstance().getUsername());
        protocolMapper.setConsentText(CommonString.getInstance().getProtocolMapperConsentTextUsername());
        protocolMapper.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
        protocolMapper.setConsentRequired(true); 
        protocolMapper.setProtocolMapper(CommonString.getInstance().getProtocolMapperPropertyMapper()); 
        protocolMapper.setConfig(Util.getInstance().buildProtocolMap(CommonString.getInstance().getUsername(),
                                                                        CommonString.getInstance().getPreferredUsername())); 
        protocolMappers.add(protocolMapper); 
         

        protocolMapper = new ProtocolMapperRepresentation();
        protocolMapper.setName(CommonString.getInstance().getProtocolMapperNameFalimyName());
        protocolMapper.setConsentText(CommonString.getInstance().getProtocolMapperConsentTextFamilyName());
        protocolMapper.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
        protocolMapper.setConsentRequired(true);
  
        protocolMapper.setProtocolMapper(CommonString.getInstance().getProtocolMapperPropertyMapper()); 
        protocolMapper.setConfig(Util.getInstance().buildProtocolMap(CommonString.getInstance().getTokenLastName(), 
                                                                        CommonString.getInstance().getTokenFamilyName())); 
        protocolMappers.add(protocolMapper); 
         
        protocolMapper = new ProtocolMapperRepresentation();
        protocolMapper.setName(CommonString.getInstance().getProtocolMapperNameGivenName());
        protocolMapper.setConsentText(CommonString.getInstance().getProtocolMapperConsentTextGivenName());
        protocolMapper.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
        protocolMapper.setConsentRequired(true);
  
        protocolMapper.setProtocolMapper(CommonString.getInstance().getProtocolMapperPropertyMapper()); 
        protocolMapper.setConfig(Util.getInstance().buildProtocolMap(CommonString.getInstance().getTokenFirstName(), 
                                                                        CommonString.getInstance().getTokenGivenName())); 
        protocolMappers.add(protocolMapper);  
        clientRepresentation.setProtocolMappers(protocolMappers); 
        
        
        List<String> redirectURIs = new ArrayList<>();
        redirectURIs.add(CommonString.getInstance().getRedirectFrontEndURL());
        clientRepresentation.setRedirectUris(redirectURIs);
         
        keycloakClient.realm(realmName).clients().create(clientRepresentation);  
    } 
 
    private void createRealm() { 
        logger.info("createRealm"); 
        
//        buildRealm(); 
        RealmRepresentation realmRepresenttion = new RealmRepresentation();
        realmRepresenttion.setRealm(realmName);
        realmRepresenttion.setDisplayName(realmName);
        realmRepresenttion.setSslRequired(CommonString.getInstance().getNone()); 
        realmRepresenttion.setDuplicateEmailsAllowed(false);
        
        realmRepresenttion.setEventsEnabled(true);
        realmRepresenttion.setAdminEventsDetailsEnabled(Boolean.TRUE);
        realmRepresenttion.setAdminEventsEnabled(Boolean.TRUE);
        realmRepresenttion.setEditUsernameAllowed(Boolean.TRUE);
        
        realmRepresenttion.setAccessCodeLifespanUserAction(90000);

        Map<String, String> smtpServerMap = new HashMap<>();
        smtpServerMap.put(CommonString.getInstance().getHost(), mailHost);
        smtpServerMap.put(CommonString.getInstance().getPort(), mailPort);
        smtpServerMap.put(CommonString.getInstance().getFrom(), mailFrom);
        smtpServerMap.put(CommonString.getInstance().getSSL(), Boolean.FALSE.toString());
        smtpServerMap.put(CommonString.getInstance().getStrtTTLS(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getAuth(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getUser(), emailUserName);
        smtpServerMap.put(CommonString.getInstance().getPassword(), emailPassword);
       
        realmRepresenttion.setSmtpServer(smtpServerMap); 
        realmRepresenttion.setPasswordPolicy("hashIterations and specialChars and length and notUsername and passwordHistory");
        realmRepresenttion.setEnabled(true);
         
        keycloakClient.realms().create(realmRepresenttion); 
    }

    private void buildRealm() {
        keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthURL) 
                .realm(CommonString.getInstance().getMastRealm())
                .username(CommonString.getInstance().getMasterAdminUsrname()) 
                .password(CommonString.getInstance().getMasterAdminPassword()) 
                .clientId(CommonString.getInstance().getAdminClientId())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
    }
    
    private void uploadProperties() {
        
        Properties prop = new Properties();
        InputStream input = null; 
        try {  
            input = new FileInputStream(CommonString.getInstance().getConfigProperties());
            prop.load(input);
            keycloakAuthURL = prop.getProperty(CommonString.getInstance().getKeycloakAuthURL());
//            realmName = prop.getProperty(CommonString.getInstance().getRealmName());
            
            mailHost = prop.getProperty(CommonString.getInstance().getMailServerHost());
            mailPort = prop.getProperty(CommonString.getInstance().getMailServerPort());
            mailFrom = prop.getProperty(CommonString.getInstance().getFrom());
            emailUserName = prop.getProperty(CommonString.getInstance().getMailServerUsername());
            emailPassword = prop.getProperty(CommonString.getInstance().getMailServerPassword());
            
            superUsername = prop.getProperty(CommonString.getInstance().getSuperUsername());
            superPassword = prop.getProperty(CommonString.getInstance().getSuperPassword());
        } catch (IOException ex) { 
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) { 
                }
            }
        }
    }
}
