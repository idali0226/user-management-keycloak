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
import se.nrm.dina.user.management.keycloak.KeycloakClient; 
import se.nrm.dina.user.management.keycloak.properties.ConfigurationProperties;
import se.nrm.dina.user.management.logic.RealmManagement;
import se.nrm.dina.user.management.utils.AccountStatus;
import se.nrm.dina.user.management.utils.CommonString; 
import se.nrm.dina.user.management.utils.Util;

/**
 *
 * @author idali
 * 
 * KeycloakSetup setup initial data for keycloak to run.
 * 
 * The following data are set during the setup process:
 * 
 * <ul>
 *  <li>Create realm</li>
 *  <li>Set password policies</li>
 *  <li>Set mail server</li>
 *  <li>Create realm roles</li>
 *  <li>Create rest endpoint client</li>
 *  <li>Create roles for rest endpoint client</li>
 *  <li>Create user-management client</li>
 *  <li>Create roles for user-management client</li>
 *  <li>Create an initial user (Super admin)</li>
 *  <li>Create an initial user (Admin)</li>
 *  <li>Create an initial user (User)</li>
 * </ul> 
 * 
 */
@Startup                            // initialize ejb at deployment time
@Singleton
@Slf4j
public class KeycloakSetup implements Serializable { 
    
    private static final String REGEX = ".*/(.*)$";
    private static final String PURPOSE_SUPER_ADMIN = "Super admin";
    private static final String PURPOSE = "Testing"; 
 
    private RealmsResource realmResources;
    private RealmResource realmResource;
    
    @Inject
    @KeycloakClient
    private Keycloak keycloakClient;
    
    @Inject
    private RealmManagement realmManagement;
    
    @Inject
    private ConfigurationProperties config;
  
    public KeycloakSetup() {
    }

    /**
     * init method runs when application server starts up and user-management start to deploy
     * 
     */
    @PostConstruct
    public void init() {
        log.info("init");  
        
        realmResources = realmManagement.getRealmResources();
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
            createInitialUser(config.getSuperAdminUsername(), config.getSuperAdminPassword(), config.getSuperAdminFirstname(), 
                              config.getSuperAdminLastname(), PURPOSE_SUPER_ADMIN, CommonString.getInstance().getSuperAdminRole(), 
                              CommonString.getInstance().getAdminRole());
            
            // Create admin
            createInitialUser(config.getAdminUsername(), config.getAdminPassword(), config.getAdminFirstname(), config.getAdminLastname(), 
                              PURPOSE, CommonString.getInstance().getAdminRole(), CommonString.getInstance().getAdminRole());
            
            // Create user
            createInitialUser(config.getUserUsername(), config.getUserPassword(), config.getUserFirstname(), config.getUserLastname(), 
                              PURPOSE, CommonString.getInstance().getUserRole(), CommonString.getInstance().getAdminRole());
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
//        redirectURIs.add(CommonString.getInstance().getRedirectFrontEndURL());
        redirectURIs.add(CommonString.getInstance().getRedirectFrontEndURLVirturalHost());
    
        clientRepresentation.setRedirectUris(redirectURIs);
         
        realmResource.clients().create(clientRepresentation);  
    } 
 
    private void createRealm() { 
        log.info("createRealm"); 
         
        RealmRepresentation realmRepresenttion = new RealmRepresentation();
        realmRepresenttion.setRealm(config.getRealm());
        realmRepresenttion.setDisplayName(config.getRealm());
        realmRepresenttion.setSslRequired(CommonString.getInstance().getNone()); 
        realmRepresenttion.setDuplicateEmailsAllowed(false);
        
        realmRepresenttion.setEventsEnabled(true);
        realmRepresenttion.setAdminEventsDetailsEnabled(Boolean.TRUE);
        realmRepresenttion.setAdminEventsEnabled(Boolean.TRUE);
        realmRepresenttion.setEditUsernameAllowed(Boolean.TRUE);
        
        realmRepresenttion.setAccessCodeLifespanUserAction(90000); 
        
        realmRepresenttion.setSmtpServer(setupMailServer()); 
        realmRepresenttion.setPasswordPolicy(config.getPasswordPolicies());
        realmRepresenttion.setEnabled(true);
         
        realmResources.create(realmRepresenttion); 
    }
    
    private Map<String, String> setupMailServer() {
        log.info("setupMailServer");
          
        Map<String, String> smtpServerMap = new HashMap<>();
        smtpServerMap.put(CommonString.getInstance().getHost(), config.getEmailHost());
        smtpServerMap.put(CommonString.getInstance().getPort(), config.getEmilPort());
        smtpServerMap.put(CommonString.getInstance().getFrom(), config.getEmailFrom());
        smtpServerMap.put(CommonString.getInstance().getSSL(), Boolean.FALSE.toString());
        smtpServerMap.put(CommonString.getInstance().getStrtTTLS(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getAuth(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getUser(), config.getEmailUsername());
        smtpServerMap.put(CommonString.getInstance().getPassword(), config.getEmailPassword()); 
        return smtpServerMap;
    }
 
    private void initRealmResources() {
        realmResources = keycloakClient.realms();
    }
    
    private void initRealmResource() {
        realmResource = keycloakClient.realm(config.getRealm());
    }
    
    private boolean isRealmExist() {
      
        return realmResources.findAll().stream() 
                            .anyMatch(realm -> realm.getDisplayName().equals(config.getRealm()));  
    }
    
        
    private void createRealmRoles(String role, String roleDescription) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role); 
        roleRepresentation.setDescription(roleDescription);
       
        realmResource.roles().create(roleRepresentation); 
    } 
}
 