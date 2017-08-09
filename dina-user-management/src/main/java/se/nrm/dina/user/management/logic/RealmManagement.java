/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;  
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject; 
import javax.json.JsonObject;
import org.keycloak.admin.client.Keycloak; 
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation; 
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.logic.helpers.KeycloakClientHelper;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
public class RealmManagement implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());  
    
    private Keycloak keycloakClient;
    
    private String REALM_NAME;
    private final String ENV_VAR_REALM_NAME = "REALM_NAME";
    private final String ENV_KEYCLOAK_URI = "KEYCLOAK_URI";
    private final String MAIL_HOST = "MAIL_HOST";
    private final String MAIL_PORT = "MAIL_PORT";
    private final String MAIL_USERNAME = "MAIL_USERNAME";
    private final String MAIL_PASSWORD = "MAIL_PASSWORD";
    private final String MAIL_FROM = "MAIL_FROM"; 
    private final String SUPER_ADMIN_USERNAME = "SUPER_USERNAME";
    private final String SUPER_ADMIN_PASSWORD = "SUPER_PASSWORD";
    
    private final String PASSWORD_POLICIES = "hashIterations and specialChars and length";
    
    @Inject
    public JsonConverter json;
    
    public RealmManagement() {  
    }
    
    @PostConstruct
    public void init() {
        logger.info("init");
        
        REALM_NAME = System.getenv(ENV_VAR_REALM_NAME);  
        logger.info("realmName : {}", REALM_NAME);
        keycloakClient = KeycloakClientHelper.getInstance().buildKeycloakClient();
     //   buildKeycloakClient();
    }
    
    public boolean isDinaRealmExists() {
        return keycloakClient.realms().findAll().stream() 
                             .anyMatch(realm -> realm.getDisplayName().equals(REALM_NAME));  
    }

    public void buildRealmRoles() {
        KeycloakClientHelper.getInstance().buildRealmRoleMap().entrySet().stream()
                .forEach(r -> {
                    RoleRepresentation roleRepresentation = new RoleRepresentation();
                    roleRepresentation.setName(r.getKey());
                    roleRepresentation.setDescription(r.getValue());

                    keycloakClient.realm(REALM_NAME).roles().create(roleRepresentation);
                });
    }

    public void createRealm() { 
        logger.info("createRealm"); 
         
        RealmRepresentation realmRepresenttion = new RealmRepresentation();
        realmRepresenttion.setRealm(REALM_NAME);
        realmRepresenttion.setDisplayName(REALM_NAME);
        realmRepresenttion.setSslRequired(CommonString.getInstance().getNone()); 
        realmRepresenttion.setDuplicateEmailsAllowed(false);
        
        realmRepresenttion.setEventsEnabled(true);
        realmRepresenttion.setAdminEventsDetailsEnabled(Boolean.TRUE);
        realmRepresenttion.setAdminEventsEnabled(Boolean.TRUE);
        realmRepresenttion.setEditUsernameAllowed(Boolean.TRUE);
        
        realmRepresenttion.setAccessCodeLifespanUserAction(90000); 
        
        realmRepresenttion.setSmtpServer(KeycloakClientHelper.getInstance().setupMailServer()); 
        realmRepresenttion.setPasswordPolicy(PASSWORD_POLICIES);
        realmRepresenttion.setEnabled(true);
         
        keycloakClient.realms().create(realmRepresenttion); 
    }
    
    public void createClient(ClientRepresentation clientRepresentation) {
        keycloakClient.realm(REALM_NAME).clients().create(clientRepresentation);
    }
    
    public void createClientRole(String clientId, String role, String description) { 
        RoleRepresentation clientRoleRepresentation = new RoleRepresentation();
        clientRoleRepresentation.setName(role);
        clientRoleRepresentation.setDescription(description);
        
        clientRoleRepresentation.setClientRole(true);
        
        RealmResource realmResource = keycloakClient.realm(REALM_NAME);
        realmResource.clients().findByClientId(clientId)
                .forEach(c -> realmResource.clients().get(c.getId())
                        .roles()
                        .create(clientRoleRepresentation)); 
    }
  
    public JsonObject getRealmByRealmName(String realmName) { 
        RealmResource realmResource = keycloakClient.realm(realmName);
         
        List<RoleRepresentation> roleRepresentations = realmResource.roles().list().stream()
                                                                        .filter(filterDefaultRealmRoles())
                                                                        .collect(Collectors.toList());
         
        return json.converterRealm(realmResource.toRepresentation(), roleRepresentations);  
    }
    

 
//    public JsonObject getRealmByRealmName(String realmName) {
//      
//        
//        RealmResource realmResource = keycloakClient.realm(realmName); 
//        List<RoleRepresentation> roleRepresentations = realmResource.roles().list();
//        List<String> realmRoles = roleRepresentations.stream()
//                                                     .filter(filterDefaultRealmRoles())
//                                                     .map(r -> r.getName())
//                                                     .collect(Collectors.toList());
//                 
//        ClientsResource clientsResource = realmResource.clients();
//        List<ClientRepresentation> clientRepresentations = clientsResource.findAll();
//        
//        ClientRepresentation dinaRestClientRepresentation = getClientRepresentation(clientRepresentations, 
//                                                                                    CommonString.getInstance().getDinaRestClientId());
//        List<String> dinaRestRoles = Arrays.asList(dinaRestClientRepresentation.getDefaultRoles());
//        
//        ClientRepresentation userManagementClientRepresentation = getClientRepresentation(clientRepresentations, 
//                                                                                          CommonString.getInstance().getUserManagementClientId());
//        List<String> userManagementRoles = Arrays.asList(userManagementClientRepresentation.getDefaultRoles());
//        Map<String, List<String>> roleMap = new HashMap<>();
//        roleMap.put(CommonString.getInstance().getDinaRestClientId(), dinaRestRoles);
//        roleMap.put(CommonString.getInstance().getUserManagementClientId(), userManagementRoles);
//        
//        
//        return null;
//    }
    
//    private ClientRepresentation getClientRepresentation(List<ClientRepresentation> clientRepresentations, String clientId) {
//        return clientRepresentations.stream() 
//                                    .filter(clientRepresentationPredicate(clientId)) 
//                                    .findFirst() 
//                                    .get();
//    }
//    
    private static Predicate<RoleRepresentation> filterDefaultRealmRoles() {
        return r -> !(r.getName().equals("uma_authorization") || r.getName().equals("offline_access") || r.getName().equals("disabled_user"));
    }
//    
//    private static Predicate<ClientRepresentation> clientRepresentationPredicate(String clientId) {
//        return c -> c.getClientId().equals(clientId);
//    }
  
    @PreDestroy
    public void preDestroy() {
        logger.info("preDestroy"); 
        if (keycloakClient != null) {
            keycloakClient.close();
            logger.info("keycloakClient is closed");
        }
    }
}
