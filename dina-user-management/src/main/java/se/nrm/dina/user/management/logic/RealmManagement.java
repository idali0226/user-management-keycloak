/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.json.JsonObject;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder; 
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource; 
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
public class RealmManagement implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());  
    
    private Keycloak keycloakClient;
    
    @Inject
    public JsonConverter json;
    
    public RealmManagement() {
        
    }
    
    public void createRealm(String jsonString) {
        buildKeycloakClient();
        
        JsonObject dataJson = json.readInJson(jsonString).getJsonObject(CommonString.getInstance().getData());
        JsonObject attributesJson = dataJson.getJsonObject(CommonString.getInstance().getAttributes());
         
        String realmName = attributesJson.getString("realm_name");
        String mailHost = attributesJson.getString("mail_host");
        String mailPort = attributesJson.getString("mail_post"); 
        String mailFrom = attributesJson.getString("mail_from"); 
        String mailUsername = attributesJson.getString("mail_username");
        String mailPassword = attributesJson.getString("mail_password");
        
        RealmRepresentation realmRepresenttion = new RealmRepresentation();
        realmRepresenttion.setRealm(realmName);
        realmRepresenttion.setDisplayName(realmName);
        realmRepresenttion.setSslRequired(CommonString.getInstance().getNone()); 
        realmRepresenttion.setDuplicateEmailsAllowed(false);
        
        realmRepresenttion.setEventsEnabled(true);
        realmRepresenttion.setAdminEventsDetailsEnabled(Boolean.TRUE);
        realmRepresenttion.setAdminEventsEnabled(Boolean.TRUE);
        realmRepresenttion.setEditUsernameAllowed(Boolean.TRUE);

        Map<String, String> smtpServerMap = new HashMap<>();
        smtpServerMap.put(CommonString.getInstance().getHost(), mailHost);
        smtpServerMap.put(CommonString.getInstance().getPort(), mailPort);
        smtpServerMap.put(CommonString.getInstance().getFrom(), mailFrom);
        smtpServerMap.put(CommonString.getInstance().getSSL(), Boolean.FALSE.toString());
        smtpServerMap.put(CommonString.getInstance().getStrtTTLS(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getAuth(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getUser(), mailUsername);
        smtpServerMap.put(CommonString.getInstance().getPassword(), mailPassword);
        
        realmRepresenttion.setSmtpServer(smtpServerMap);
        keycloakClient.realms().create(realmRepresenttion);
    }
    
    public void getRealmEvent(String realmName) {
        buildKeycloakClient();
        List<EventRepresentation> events = keycloakClient.realm(realmName).getEvents();
        events.stream()
                .forEach(e -> {
                    logger.info("event type : {}", e.getType());
                    logger.info("client : {}", e.getClientId());
                    logger.info("event detial : {}", e.getDetails());
       
                });
        
        List<AdminEventRepresentation> adminEvents = keycloakClient.realm(realmName).getAdminEvents();
        adminEvents.stream()
                .forEach(e -> {
                    logger.info("operation type : {}", e.getOperationType());
                    logger.info("resource type : {}", e.getResourceType()); 
                });
        
        
         
    }
    
    public JsonObject getRealmByRealmName(String realmName) {
        buildKeycloakClient();
        
        RealmResource realmResource = keycloakClient.realm(realmName); 
        List<RoleRepresentation> roleRepresentations = realmResource.roles().list();
        List<String> realmRoles = roleRepresentations.stream()
                                                     .filter(realmRolesPredicate())
                                                     .map(r -> r.getName())
                                                     .collect(Collectors.toList());
                 
        ClientsResource clientsResource = realmResource.clients();
        List<ClientRepresentation> clientRepresentations = clientsResource.findAll();
        
        ClientRepresentation dinaRestClientRepresentation = getClientRepresentation(clientRepresentations, 
                                                                                    CommonString.getInstance().getDinaRestClientId());
        List<String> dinaRestRoles = Arrays.asList(dinaRestClientRepresentation.getDefaultRoles());
        
        ClientRepresentation userManagementClientRepresentation = getClientRepresentation(clientRepresentations, 
                                                                                          CommonString.getInstance().getUserManagementClientId());
        List<String> userManagementRoles = Arrays.asList(userManagementClientRepresentation.getDefaultRoles());
        Map<String, List<String>> roleMap = new HashMap<>();
        roleMap.put(CommonString.getInstance().getDinaRestClientId(), dinaRestRoles);
        roleMap.put(CommonString.getInstance().getUserManagementClientId(), userManagementRoles);
        
        
        return null;
    }
    
    private ClientRepresentation getClientRepresentation(List<ClientRepresentation> clientRepresentations, String clientId) {
        return clientRepresentations.stream() 
                                    .filter(clientRepresentationPredicate(clientId)) 
                                    .findFirst() 
                                    .get();
    }
    
    private static Predicate<RoleRepresentation> realmRolesPredicate() {
        return r -> !r.getName().equals("uma_authorization") && !r.getName().equals("offline_access");
    }
    
    private static Predicate<ClientRepresentation> clientRepresentationPredicate(String clientId) {
        return c -> c.getClientId().equals(clientId);
    }
     

    private void buildKeycloakClient() {   
        keycloakClient = KeycloakBuilder.builder()
                                        .serverUrl(CommonString.getInstance().getKeyCloakLUrl()) //
                                        .realm(CommonString.getInstance().getMastRealm())//
                                        .username(CommonString.getInstance().getMasterAdminUsrname()) //
                                        .password(CommonString.getInstance().getMasterAdminPassword()) //
                                        .clientId(CommonString.getInstance().getAdminClientId())
                                        .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
                                        .build();
    }
}
