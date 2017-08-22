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
import javax.inject.Inject;
import javax.ws.rs.core.Response;   
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak; 
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
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;
import se.nrm.dina.user.management.keycloak.KeycloakClient; 
import se.nrm.dina.user.management.utils.AccountStatus;
import se.nrm.dina.user.management.utils.CommonString; 
import se.nrm.dina.user.management.utils.Util;

/**
 *
 * @author idali
 */
@Startup                            // initialize ejb at deployment time
@Singleton
@Slf4j
public class KeycloakSetup implements Serializable { 
    
    private static final String REGEX = ".*/(.*)$";
    private static final String PURPOSE_SUPER_ADMIN = "Super admin";
    private static final String PURPOSE = "Testing"; 
   
    private final String PASSWORD_POLICIES = "hashIterations and specialChars and length";
     
    private RealmsResource realmResources;
    private RealmResource realmResource;
   
    @Inject
    @ConfigurationValue("swarm.email.host")
    private String emailHost;
        
    @Inject
    @ConfigurationValue("swarm.email.port")
    private String emailPort;
            
    @Inject
    @ConfigurationValue("swarm.email.username")
    private String emailUsername;
                
    @Inject
    @ConfigurationValue("swarm.email.password")
    private String emailPassword;

    @Inject
    @ConfigurationValue("swarm.email.from")
    private String emailFrom;

    @Inject
    @ConfigurationValue("swarm.user.superadmin.username")
    private String superUsername;

    @Inject
    @ConfigurationValue("swarm.user.superadmin.password")
    private String superUserPassword;

    @Inject
    @ConfigurationValue("swarm.user.superadmin.firstname")
    private String superUserFirstname;

    @Inject
    @ConfigurationValue("swarm.user.superadmin.lastname")
    private String superUserLastname;

    @Inject
    @ConfigurationValue("swarm.user.admin.username")
    private String adminUsername;

    @Inject
    @ConfigurationValue("swarm.user.admin.password")
    private String adminPassword;  
    
    @Inject
    @ConfigurationValue("swarm.user.admin.firstname")
    private String adminFirstname;

    @Inject
    @ConfigurationValue("swarm.user.admin.lastname")
    private String adminLastname;
    
    @Inject
    @ConfigurationValue("swarm.user.user.username")
    private String userUsername;
    
    @Inject
    @ConfigurationValue("swarm.user.user.password")
    private String userPassword;  
    
    @Inject
    @ConfigurationValue("swarm.user.user.firstname")
    private String userFirstname;

    @Inject
    @ConfigurationValue("swarm.user.user.lastname")
    private String userLastname;
     
    @Inject
    @KeycloakClient
    private Keycloak keycloakClient;
    
    @Inject
    @KeycloakClient
    private String dinaRealm;

    public KeycloakSetup() {
    }

    /**
     * 
     * 
     */
    @PostConstruct
    public void init() {
        log.info("init : {} -- {}", keycloakClient, dinaRealm);  
        
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
            
            // Create super admin
            createInitialUser(superUsername, superUserPassword, superUserFirstname, superUserLastname, PURPOSE_SUPER_ADMIN, 
                                CommonString.getInstance().getSuperAdminRole(), CommonString.getInstance().getAdminRole());
            
            // Create admin
            createInitialUser(adminUsername, adminPassword, adminFirstname, adminLastname, PURPOSE, 
                                CommonString.getInstance().getAdminRole(), CommonString.getInstance().getAdminRole());
            
            // Create user
            createInitialUser(userUsername, userPassword, userFirstname, userLastname, PURPOSE,
                                CommonString.getInstance().getUserRole(), CommonString.getInstance().getAdminRole());
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
        realmRoleMap.put(CommonString.getInstance().getSuperAdminRole(), CommonString.getInstance().getSuperAdminRole());
        realmRoleMap.put(CommonString.getInstance().getDisabledUserRole(), CommonString.getInstance().getDisabledRoleDescription());
        return realmRoleMap;
    }
     
    private void createInitialUser(String username, String password, String firstname, 
                                   String lastname, String purpose, String realmRole, String clientRole) {
        log.info("createInitialUser" ); 
        
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(username);
        user.setFirstName(firstname);
        user.setLastName(lastname); 
        user.setEnabled(Boolean.TRUE);
        user.setEmailVerified(Boolean.TRUE);
        
        user.singleAttribute(CommonString.getInstance().getStatus(), AccountStatus.Enabled.name());
        user.singleAttribute(CommonString.getInstance().getPurpose(), purpose);
          
        Response response = realmResource.users().create(user); 
        
        log.info("crate user response : {}", response);
        String locationHeader = response.getHeaderString(CommonString.getInstance().getLocation());
        response.close();

        if (locationHeader != null) {
            UserResource userResource = realmResource.users().get(locationHeader.replaceAll(REGEX, "$1"));   
            resetPassword(userResource, password);
            setReamlRole(userResource, realmRole);
            setClientRole(CommonString.getInstance().getDinaRestClientId(), userResource, clientRole);
            setClientRole(CommonString.getInstance().getUserManagementClientId(), userResource, clientRole); 
        } 
    }
     
    private void setClientRole(String clientId, UserResource userResource, String role) {
 
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
                    if (rr.getName().equals(role)) {
                        newRole.add(rr); 
                    }
                });
        userResource.roles().clientLevel(cId).add(newRole);
    } 
    
    private void setReamlRole(UserResource userResource, String role) {
        List<RoleRepresentation> rrs = userResource.roles().realmLevel().listAll();
        userResource.roles().realmLevel().remove(rrs);
   
        List<RoleRepresentation> dinaRealmRoles = realmResource.roles().list();

        List<RoleRepresentation> newRole = new ArrayList<>();
        dinaRealmRoles.stream()
                .filter(drr -> !drr.getName().equals(CommonString.getInstance().getOfflineAccessRole()))
                .filter(drr -> !drr.getName().equals(CommonString.getInstance().getUmaAuthorizationRole()))
                        .forEach(drr -> { 
                            if(drr.getName().equals(role)) {
                                newRole.add(drr);
                            } 
                        });  
        userResource.roles().realmLevel().add(newRole); 
    }
    
    
    private void resetPassword(UserResource userResource, String password) { 
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
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
        log.info("createRealm"); 
         
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
        log.info("setupMailServer");
          
        Map<String, String> smtpServerMap = new HashMap<>();
        smtpServerMap.put(CommonString.getInstance().getHost(), emailHost);
        smtpServerMap.put(CommonString.getInstance().getPort(), emailPort);
        smtpServerMap.put(CommonString.getInstance().getFrom(), emailFrom);
        smtpServerMap.put(CommonString.getInstance().getSSL(), Boolean.FALSE.toString());
        smtpServerMap.put(CommonString.getInstance().getStrtTTLS(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getAuth(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getUser(), emailUsername);
        smtpServerMap.put(CommonString.getInstance().getPassword(), emailPassword); 
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
}
 