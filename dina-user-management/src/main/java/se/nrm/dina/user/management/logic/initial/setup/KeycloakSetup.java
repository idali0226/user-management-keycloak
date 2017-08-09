/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.initial.setup;
  
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import javax.annotation.PostConstruct;  
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.core.Response; 
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder; 
import org.keycloak.admin.client.resource.ClientsResource; 
import org.keycloak.admin.client.resource.RealmResource;
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
 
    private static final String REGEX = ".*/(.*)$";
    private static final String PURPOSE = "Super admin";
 
    private String keycloakAuthURL; 
    private String dinaRealm;
    private final String PASSWORD_POLICIES = "hashIterations and specialChars and length";
 
//    private final String ENV_KEYCLOAK_URI = "KEYCLOAK_URI";
//    private final String ENV_REALM_NAME = "REALM_NAME";
    
    private final String MAIL_HOST = "MAIL_HOST";
    private final String MAIL_PORT = "MAIL_PORT";
    private final String MAIL_USERNAME = "MAIL_USERNAME";
    private final String MAIL_PASSWORD = "MAIL_PASSWORD";
    private final String MAIL_FROM = "MAIL_FROM"; 
    private final String SUPER_ADMIN_USERNAME = "SUPER_USERNAME";
    private final String SUPER_ADMIN_PASSWORD = "SUPER_PASSWORD";
    
    private Keycloak keycloakClient; 
    private RealmsResource realmResources;
    private RealmResource realmResource;

    public KeycloakSetup() {
    }

    @PostConstruct
    public void init() {
        logger.info("init");

        keycloakAuthURL = System.getenv(CommonString.getInstance().getEnvKeycloakURI()); 
        logger.info("keycloakAuthURL : {}", keycloakAuthURL); 
        if (StringUtils.isEmpty(keycloakAuthURL)) {
            keycloakAuthURL = "http://localhost:8080/auth";
        } 
        dinaRealm = System.getenv(CommonString.getInstance().getEnvRealmName()); 
        logger.info("dinaRealm : {}", dinaRealm);
        buildKeycloakClient();
        initRealmResources(); 
        if(!isRealmExist()) {   
            createRealm(); 
            initRealmResource();
            
            setupRealmRoleMap().entrySet()
                                    .stream()
                                        .forEach(r -> {
                                            createRealmRoles(r.getKey(), r.getValue());
                                        });
              
            createEndpointClient();
            createUserManagementClient();

            setupBasicRoleMap().entrySet()
                                .stream()
                                .forEach(r -> {
                                    createClientRole(CommonString.getInstance().getDinaRestClientId(), r.getKey(), r.getValue());
                                    createClientRole(CommonString.getInstance().getUserManagementClientId(), r.getKey(), r.getValue());
                                }); 
            createInitialUser();
        }
        if (keycloakClient != null) {
            keycloakClient.close();
            logger.info("keycloakClient is closed");
        }
    }

    private Map<String, String> setupBasicRoleMap() {
        Map<String, String> basicRoleMap = new HashMap();
        basicRoleMap.put(CommonString.getInstance().getAdminRole(), CommonString.getInstance().getAdminRoleDescription());
        basicRoleMap.put(CommonString.getInstance().getUserRole(), CommonString.getInstance().getUserRoleDescription());
        return basicRoleMap;
    }
    
    private Map<String, String> setupRealmRoleMap() {  
        Map<String, String> realmRoleMap = setupBasicRoleMap();
        realmRoleMap.put(CommonString.getInstance().getDisabledUserRole(), CommonString.getInstance().getDisabledRoleDescription());
        return realmRoleMap;
    }
     
    private void createInitialUser() {
        logger.info("createInitialUser");
        String superUsername = System.getenv(SUPER_ADMIN_USERNAME);
        
        logger.info("username : {}", superUsername);
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
        statusList.add(AccountStatus.Enabled.name());
        attributes.put(CommonString.getInstance().getStatus(), statusList);  
        user.setAttributes(attributes);    
         
        Response response = realmResource.users().create(user); 
        
        logger.info("crate user response : {}", response);
        String locationHeader = response.getHeaderString(CommonString.getInstance().getLocation());
        response.close();

        if (locationHeader != null) {
            UserResource userResource = realmResource.users().get(locationHeader.replaceAll(REGEX, "$1"));   
            resetPassword(userResource);
            setReamlRole(userResource);
            setClientRole(CommonString.getInstance().getDinaRestClientId(), userResource);
            setClientRole(CommonString.getInstance().getUserManagementClientId(), userResource);
        } 
    }
     
    private void setClientRole(String clientId, UserResource userResource) {
 
        ClientsResource clientsResource = realmResource.clients();
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
   
        List<RoleRepresentation> dinaRealmRoles = realmResource.roles().list();

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
        String superPassword = System.getenv(SUPER_ADMIN_PASSWORD);
        
        logger.info("password : {}", superPassword);
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
        realmResource.clients().findByClientId(clientId).forEach(clientRepresentation ->
            realmResource.clients().get(clientRepresentation.getId()).roles().create(clientRoleRepresentation)
        ); 
    }
 
    private void createUserManagementClient() {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(CommonString.getInstance().getUserManagementClientId());
        clientRepresentation.setName(CommonString.getInstance().getUserManagementClientName());
        clientRepresentation.setDescription("Management user account in keycloak");
        clientRepresentation.setEnabled(Boolean.TRUE);
        clientRepresentation.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
        clientRepresentation.setDirectAccessGrantsEnabled(Boolean.FALSE); 
        clientRepresentation.setBearerOnly(Boolean.TRUE);
         
        realmResource.clients().create(clientRepresentation);  
    }
    
    private void setupProtocolMapper(List<ProtocolMapperRepresentation> protocolMappers,   
                                     String name, String consentText, String protocol, 
                                     String protocolMapper, Map<String, String> configMap) { 
        
        ProtocolMapperRepresentation protocolMapperRepresentation = new ProtocolMapperRepresentation();
        protocolMapperRepresentation.setName(name);
        protocolMapperRepresentation.setConsentText(consentText);
        protocolMapperRepresentation.setProtocol(protocol);
        protocolMapperRepresentation.setConsentRequired(true); 
        protocolMapperRepresentation.setProtocolMapper(protocolMapper); 
        protocolMapperRepresentation.setConfig(configMap); 
        protocolMappers.add(protocolMapperRepresentation);
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
        setupProtocolMapper(protocolMappers, 
                            CommonString.getInstance().getRealmRoleClaimName(), 
                            CommonString.getInstance().getProtocolMapperConsentTextRealmRole(), 
                            CommonString.getInstance().getOpenIdConnectionProtocol(), 
                            CommonString.getInstance().getProtocolMapperRealmRoleMapper(), 
                            Util.getInstance().buildProtocolMap(
                                    CommonString.getInstance().getRealmRoleClaimName(),
                                    CommonString.getInstance().getRealmRoleClaimName()));
        
        setupProtocolMapper(protocolMappers, 
                            CommonString.getInstance().getEmail(),
                            CommonString.getInstance().getProtocolMapperConsentTextEmail(), 
                            CommonString.getInstance().getOpenIdConnectionProtocol(), 
                            CommonString.getInstance().getProtocolMapperPropertyMapper(), 
                            Util.getInstance().buildProtocolMap(CommonString.getInstance().getEmail(),
                                                                CommonString.getInstance().getEmail()));
        
        setupProtocolMapper(protocolMappers, 
                            CommonString.getInstance().getUsername(),
                            CommonString.getInstance().getProtocolMapperConsentTextUsername(), 
                            CommonString.getInstance().getOpenIdConnectionProtocol(), 
                            CommonString.getInstance().getProtocolMapperPropertyMapper(), 
                            Util.getInstance().buildProtocolMap(CommonString.getInstance().getUsername(),
                                                                CommonString.getInstance().getPreferredUsername()));
        
        setupProtocolMapper(protocolMappers, 
                            CommonString.getInstance().getProtocolMapperNameFalimyName(),
                            CommonString.getInstance().getProtocolMapperConsentTextFamilyName(), 
                            CommonString.getInstance().getOpenIdConnectionProtocol(), 
                            CommonString.getInstance().getProtocolMapperPropertyMapper(), 
                            Util.getInstance().buildProtocolMap(CommonString.getInstance().getTokenLastName(), 
                                                                CommonString.getInstance().getTokenFamilyName()));
        
        setupProtocolMapper(protocolMappers, 
                            CommonString.getInstance().getProtocolMapperNameGivenName(),
                            CommonString.getInstance().getProtocolMapperConsentTextGivenName(), 
                            CommonString.getInstance().getOpenIdConnectionProtocol(), 
                            CommonString.getInstance().getProtocolMapperPropertyMapper(), 
                            Util.getInstance().buildProtocolMap(CommonString.getInstance().getTokenFirstName(), 
                                                                CommonString.getInstance().getTokenGivenName()));
        clientRepresentation.setProtocolMappers(protocolMappers);  
         
        List<String> redirectURIs = new ArrayList<>();
        redirectURIs.add(CommonString.getInstance().getRedirectFrontEndURL());
        clientRepresentation.setRedirectUris(redirectURIs);
         
        realmResource.clients().create(clientRepresentation);  
    } 
 
    private void createRealm() { 
        logger.info("createRealm"); 
         
        RealmRepresentation realmRepresenttion = new RealmRepresentation();
        realmRepresenttion.setRealm(dinaRealm);
        realmRepresenttion.setDisplayName(dinaRealm);
        realmRepresenttion.setSslRequired(CommonString.getInstance().getNone()); 
        realmRepresenttion.setDuplicateEmailsAllowed(false);
        
        realmRepresenttion.setEventsEnabled(true);
        realmRepresenttion.setAdminEventsDetailsEnabled(Boolean.TRUE);
        realmRepresenttion.setAdminEventsEnabled(Boolean.TRUE);
        realmRepresenttion.setEditUsernameAllowed(Boolean.TRUE);
        
        realmRepresenttion.setAccessCodeLifespanUserAction(90000); 
        
        realmRepresenttion.setSmtpServer(setupMailServer()); 
        realmRepresenttion.setPasswordPolicy(PASSWORD_POLICIES);
        realmRepresenttion.setEnabled(true);
         
        realmResources.create(realmRepresenttion); 
    }
    
    private Map<String, String> setupMailServer() {
        logger.info("setupMailServer");
          
        Map<String, String> smtpServerMap = new HashMap<>();
        smtpServerMap.put(CommonString.getInstance().getHost(), System.getenv(MAIL_HOST));
        smtpServerMap.put(CommonString.getInstance().getPort(), System.getenv(MAIL_PORT));
        smtpServerMap.put(CommonString.getInstance().getFrom(), System.getenv(MAIL_FROM));
        smtpServerMap.put(CommonString.getInstance().getSSL(), Boolean.FALSE.toString());
        smtpServerMap.put(CommonString.getInstance().getStrtTTLS(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getAuth(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getUser(), System.getenv(MAIL_USERNAME));
        smtpServerMap.put(CommonString.getInstance().getPassword(), System.getenv(MAIL_PASSWORD));
        logger.info("mail config : {}", smtpServerMap);
        return smtpServerMap;
    }
 
    private void initRealmResources() {
        realmResources = keycloakClient.realms();
    }
    
    private void initRealmResource() {
        realmResource = keycloakClient.realm(dinaRealm);
    }
    
    private boolean isRealmExist() {
        return realmResources.findAll().stream() 
                            .anyMatch(realm -> realm.getDisplayName().equals(dinaRealm));  
    }
    
        
    private void createRealmRoles(String role, String roleDescription) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role); 
        roleRepresentation.setDescription(roleDescription);
       
        realmResource.roles().create(roleRepresentation); 
    }
  
    private void buildKeycloakClient() {
        keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthURL) 
                .realm(CommonString.getInstance().getMastRealm())
                .username(CommonString.getInstance().getMasterAdminUsrname()) 
                .password(CommonString.getInstance().getMasterAdminPassword()) 
                .clientId(CommonString.getInstance().getAdminClientId())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
    } 
}
 