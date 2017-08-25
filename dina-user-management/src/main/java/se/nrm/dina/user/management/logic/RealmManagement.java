/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;  
import java.util.ArrayList; 
import java.util.List; 
import java.util.function.Predicate;
import java.util.stream.Collectors;  
import javax.inject.Inject; 
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j; 
import org.keycloak.admin.client.Keycloak;  
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;   
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;    
import se.nrm.dina.user.management.json.JsonConverter; 
import se.nrm.dina.user.management.keycloak.KeycloakClient;
import se.nrm.dina.user.management.keycloak.properties.ConfigurationProperties;
import se.nrm.dina.user.management.logic.helpers.KeycloakHelper;
import se.nrm.dina.user.management.utils.AccountStatus;
import se.nrm.dina.user.management.utils.CommonString; 

/**
 *
 * @author idali
 */
@Slf4j
public class RealmManagement implements Serializable {
 
    @Inject
    @KeycloakClient
    private Keycloak keycloakClient;
    
    @Inject
    public ConfigurationProperties config;
  
    @Inject
    private JsonConverter json; 
    private KeycloakHelper helper;
    
    private static final String PURPOSE_SUPER_ADMIN = "Super admin";
    private static final String PURPOSE = "Testing"; 

    public RealmManagement() { 
    }
    
    public RealmManagement(Keycloak keycloakClient) {
        this.keycloakClient = keycloakClient;
    }
 
    public RealmResource getDinaRealmResource() {
        return getRealmResources().realm(config.getRealm());
    }

    public RealmsResource getRealmResources() {
        return keycloakClient.realms();
    }
  
    public boolean isRealmExist() { 
        return getRealmResources().findAll().stream().anyMatch(realmExist());  
    }
    
    public void createClientRoles() {
        log.info("createClientRoles");
        
        helper = new KeycloakHelper();
        helper.setupBasicRoleMap().entrySet()
                                    .stream()
                                    .forEach(r -> {
                                        createClientRole(CommonString.getInstance().getDinaRestClientId(), r.getKey(), r.getValue());
                                        createClientRole(CommonString.getInstance().getUserManagementClientId(), r.getKey(), r.getValue());
                                    });
    }
    
    public void createClientRole(String clientId, String role, String description) { 
        log.info("createClientRole");
        
        RoleRepresentation clientRoleRepresentation = new RoleRepresentation();
        clientRoleRepresentation.setName(role);
        clientRoleRepresentation.setDescription(description);
        
        clientRoleRepresentation.setClientRole(true);
        getDinaRealmResource().clients().findByClientId(clientId).forEach(clientRepresentation ->
            getDinaRealmResource().clients().get(clientRepresentation.getId()).roles().create(clientRoleRepresentation)
        ); 
    }
    
    public void createRealmClient(String clientId, String clientName, String clientDescription, boolean directAccessGrantsEnabled) {
        log.info("createRealmClient");
        
        helper = new KeycloakHelper();
        ClientRepresentation client = helper.buildClientRepresentation(clientId, clientName, clientDescription, directAccessGrantsEnabled);
         
        if(directAccessGrantsEnabled) {
            List<ProtocolMapperRepresentation> protocolMappers = new ArrayList();  
            
            protocolMappers.add(helper.buildProtocolMapper(CommonString.getInstance().getRealmRoleClaimName(), 
                                                           CommonString.getInstance().getProtocolMapperConsentTextRealmRole(),  
                                                           CommonString.getInstance().getOpenIdConnectionProtocol(), 
                                                           CommonString.getInstance().getProtocolMapperRealmRoleMapper(),   
                                                           helper.buildProtocolMap(CommonString.getInstance().getRealmRoleClaimName(),
                                                                                   CommonString.getInstance().getRealmRoleClaimName())));
            
            protocolMappers.add(helper.buildProtocolMapper(CommonString.getInstance().getEmail(),
                                                           CommonString.getInstance().getProtocolMapperConsentTextEmail(), 
                                                           CommonString.getInstance().getOpenIdConnectionProtocol(), 
                                                           CommonString.getInstance().getProtocolMapperPropertyMapper(),  
                                                           helper.buildProtocolMap(CommonString.getInstance().getEmail(),
                                                                                   CommonString.getInstance().getEmail())));
             
            protocolMappers.add(helper.buildProtocolMapper(CommonString.getInstance().getUsername(),
                                                           CommonString.getInstance().getProtocolMapperConsentTextUsername(), 
                                                           CommonString.getInstance().getOpenIdConnectionProtocol(),
                                                           CommonString.getInstance().getProtocolMapperPropertyMapper(), 
                                                           helper.buildProtocolMap(CommonString.getInstance().getUsername(),
                                                                                   CommonString.getInstance().getPreferredUsername())));
            
            protocolMappers.add(helper.buildProtocolMapper(CommonString.getInstance().getProtocolMapperNameFalimyName(),
                                                           CommonString.getInstance().getProtocolMapperConsentTextFamilyName(), 
                                                           CommonString.getInstance().getOpenIdConnectionProtocol(), 
                                                           CommonString.getInstance().getProtocolMapperPropertyMapper(), 
                                                           helper.buildProtocolMap(CommonString.getInstance().getTokenLastName(), 
                                                                                   CommonString.getInstance().getTokenFamilyName())));
            
            protocolMappers.add(helper.buildProtocolMapper(CommonString.getInstance().getProtocolMapperNameGivenName(),
                                                           CommonString.getInstance().getProtocolMapperConsentTextGivenName(),
                                                           CommonString.getInstance().getOpenIdConnectionProtocol(), 
                                                           CommonString.getInstance().getProtocolMapperPropertyMapper(), 
                                                           helper.buildProtocolMap(CommonString.getInstance().getTokenFirstName(), 
                                                                                   CommonString.getInstance().getTokenGivenName())));
            
            
            client.setProtocolMappers(protocolMappers);
            List<String> redirectURIs = new ArrayList<>();
            redirectURIs.add(CommonString.getInstance().getRedirectFrontEndURL());
            redirectURIs.add(config.getUiURL());
            client.setRedirectUris(redirectURIs);
        }
        getDinaRealmResource().clients().create(client);
    }
     
    public void createRealmRoles() {
        log.info("createRealmRoles");
        
        helper = new KeycloakHelper();
        helper.setupRealmRoleMap().entrySet().stream()
                                    .forEach(r -> {
                                        createRealmRole(r.getKey(), r.getValue());
                                    });
    }
    
    public void createRealmRole(String role, String roleDescription) {
        log.info("createRealmRole");
        
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role); 
        roleRepresentation.setDescription(roleDescription);
       
        getDinaRealmResource().roles().create(roleRepresentation); 
    } 
    
    public void createRealm() {
        log.info("createRealm");
        
        helper = new KeycloakHelper();
         
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
        
        realmRepresenttion.setSmtpServer(helper.buildSMTPMailMap(config.getEmailHost(), config.getEmilPort(), 
                                                                 config.getEmailFrom(), config.getEmailUsername(), 
                                                                 config.getEmailPassword())); 
        realmRepresenttion.setPasswordPolicy(config.getPasswordPolicies());
        realmRepresenttion.setEnabled(true);
         
        getRealmResources().create(realmRepresenttion);  
    }
      
    public JsonObject getRealmByRealmName(String realmName) { 
        log.info("getRealmByRealmName");
        
        RealmResource realmResource = keycloakClient.realm(config.getRealm()); // TODO: use config property for now
        List<ClientRepresentation> clientRepresentations = realmResource.clients().findAll().stream()
                                                                        .filter(filterDefaultRealmClients())
                                                                        .collect(Collectors.toList());
         
        List<RoleRepresentation> roleRepresentations = realmResource.roles().list().stream()
                                                                        .filter(filterDefaultRealmRoles())
                                                                        .collect(Collectors.toList());
         
        return json.converterRealm(realmResource.toRepresentation(), roleRepresentations, clientRepresentations);  
    }
    
    public void createRealmInitialUsers() {
        log.info("createRealmInitialUsers");
        
        createRealmInitialUser(config.getSuperAdminUsername(), config.getSuperAdminPassword(), config.getSuperAdminFirstname(),
                               config.getSuperAdminLastname(), PURPOSE_SUPER_ADMIN, CommonString.getInstance().getSuperAdminRole(), 
                               CommonString.getInstance().getAdminRole());
        
        createRealmInitialUser(config.getAdminUsername(), config.getAdminPassword(), config.getAdminFirstname(), 
                               config.getAdminLastname(), PURPOSE, CommonString.getInstance().getAdminRole(), 
                               CommonString.getInstance().getAdminRole());
        
        createRealmInitialUser(config.getUserUsername(), config.getUserPassword(), config.getUserFirstname(), 
                               config.getUserLastname(), PURPOSE, CommonString.getInstance().getUserRole(), 
                               CommonString.getInstance().getAdminRole());
    }
    
        
    public void createRealmInitialUser( String username, String password, String firstname, 
                                        String lastname, String purpose, String realmRole, String clientRole) {
        log.info("createInitialUser" ); 
        
//        UserRepresentation user = new UserRepresentation();
//        user.setUsername(username);
//        user.setEmail(username);
//        user.setFirstName(firstname);
//        user.setLastName(lastname); 
//        user.setEnabled(Boolean.TRUE);
//        user.setEmailVerified(Boolean.TRUE);
//        
//        user.singleAttribute(CommonString.getInstance().getStatus(), AccountStatus.Enabled.name());
//        user.singleAttribute(CommonString.getInstance().getPurpose(), purpose);
        
        helper = new KeycloakHelper(); 
        Response response = getDinaRealmResource().users().create(helper.buildUserRepresentation(username, firstname, lastname, purpose, true, AccountStatus.Enabled));  
        String locationHeader = response.getHeaderString(CommonString.getInstance().getLocation());
        response.close();

        if (locationHeader != null) {
            log.info("locationHeader : {}", locationHeader);
             
            UserResource userResource = getDinaRealmResource().users().get(helper.extractUserIdFromString(locationHeader));   
            userResource.resetPassword(helper.buildCredentialRepresentation(password, false));
            
            addRealmRolesToUser(userResource, realmRole);
             
            setClientRole(CommonString.getInstance().getDinaRestClientId(), userResource, clientRole);
            setClientRole(CommonString.getInstance().getUserManagementClientId(), userResource, clientRole); 
        } 
    }
    
    public void setClientRole(String clientId, UserResource userResource, String role) {
        log.info("setClientRole");
        List<RoleRepresentation> newRole = new ArrayList<>();
        
        String id = getClientRepresentationByClientId(clientId).getId();
        List<RoleRepresentation> clientRoleRepresentation = getClientRolesById(id);
        clientRoleRepresentation.stream()
                                .forEach(rr -> {
                                    if (rr.getName().equals(role)) {
                                        newRole.add(rr); 
                                    }
                                }); 
        userResource.roles().clientLevel(id).add(newRole);
    }
     
    public List<RoleRepresentation> getClientRolesById(String id) {
        return getClientResourceById(id).roles().list();
    }
  
     
    public ClientRepresentation getClientRepresentationByClientId(String clientId) {
        return getDinaClientResources().findByClientId(clientId).get(0);
    }
    
    public ClientResource getClientResourceById(String id) {
        return getDinaClientResources().get(id);
    }
    
    public ClientsResource getDinaClientResources() {
        return getDinaRealmResource().clients();
    }
    
    public List<RoleRepresentation> getDinaRealmRoles() {
        return getDinaRealmResource().roles().list().stream()
                                                    .filter(drr -> !drr.getName().equals(CommonString.getInstance().getOfflineAccessRole()))
                                                    .filter(drr -> !drr.getName().equals(CommonString.getInstance().getUmaAuthorizationRole()))
                                                    .collect(Collectors.toList());
    }
    
    public void removeRealmRolesFromUser(UserResource userResource) {
        log.info("removeRealmRolesFromUser");
        
        List<RoleRepresentation> rrs = userResource.roles().realmLevel().listAll();
        userResource.roles().realmLevel().remove(rrs);
    }

    public void addRealmRolesToUser(UserResource userResource, String role) { 
        log.info("addRealmRolesToUser");
        
        removeRealmRolesFromUser(userResource); 
        List<RoleRepresentation> newRole = new ArrayList<>();
        getDinaRealmRoles().stream()
                .forEach(drr -> {
                    if (drr.getName().equals(role)) {
                        newRole.add(drr);
                    }
                });
        userResource.roles().realmLevel().add(newRole);
    } 
   
    private Predicate<RealmRepresentation> realmExist() {
        return r -> r.getDisplayName().equals(config.getRealm());
    }
     
    private Predicate<ClientRepresentation> filterDefaultRealmClients() {
        return c -> c.getName().startsWith("dina") || c.getName().startsWith("user");
    }
        
    private Predicate<RoleRepresentation> filterDefaultRealmRoles() { 
        return r -> !(r.getName().equals("uma_authorization") || r.getName().equals("offline_access") || r.getName().equals("disabled_user"));
    } 
}
