/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;   
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.json.JsonObject; 
import org.keycloak.admin.client.Keycloak; 
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource; 
import org.keycloak.representations.idm.ClientRepresentation;
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
public class RoleManagement implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());  
    
    private Keycloak keycloakClient; 
    private String realmName;
    private RealmResource realmResource;
    
    @Inject
    public JsonConverter json;
        
    public RoleManagement() {
        
    }
    
    @PostConstruct
    public void init() {
        logger.info("init");
        keycloakClient = KeycloakClientHelper.getInstance().buildKeycloakClient();
        realmName = System.getenv(CommonString.getInstance().getEnvRealmName()); 
        realmResource = keycloakClient.realm(realmName);
    }

    public JsonObject getRoleById(String id) {   
        RoleRepresentation roleRepresentation = realmResource.rolesById().getRole(id);
        
        String roleBelongTo = realmName;
        if (roleRepresentation.getClientRole()) {
            
            ClientsResource clientsResource = realmResource.clients(); 
            ClientRepresentation clientRepresentation = realmResource.clients().findByClientId(CommonString.getInstance().getDinaRestClientId()).get(0);

            if(isClientByRole(clientsResource, clientRepresentation.getId(), id)) {
                roleBelongTo = clientRepresentation.getName();
            } else {
                clientRepresentation = realmResource.clients().findByClientId(CommonString.getInstance().getUserManagementClientId()).get(0);
                if(isClientByRole(clientsResource, clientRepresentation.getId(), id)) {
                    roleBelongTo = clientRepresentation.getName();
                }
            }
        } 
        return json.converterRole(roleRepresentation, roleBelongTo);
    }
    
    private boolean isClientByRole(ClientsResource clientsResource, String clientId, String roleId) {  
        return clientsResource.get(clientId).roles().list().stream().anyMatch(r -> r.getId().equals(roleId)); 
    }
    
//    public JsonObject getAllRealmRoles(String realm) {
//   
//        
//        RolesResource rolesResource = keycloakClient.realm(realm).roles();
//         
//        keycloakClient.close(); 
//        return json.converterRoles(rolesResource);
//    }
    
    
    
//    public JsonObject getClientRolesByClientId(String realm, String clientId) {
//        
//        logger.info("getClientRolesByClientId : {} -- {}", realm, clientId);
//         
//        RolesResource rolesResoruce = keycloakClient.realm(realm).clients().get(clientId).roles(); 
//        return json.converterRoles(rolesResoruce);
//    }
    
//    public Keycloak getKeycloak() {
//        return keycloakClient;
//    }
    
//    public void buildKeycloakClient() {
//
//        String keycloakAuthURL = System.getenv(CommonString.getInstance().getKeycloakURI());
//        logger.info("keycloakAuthURL : {}", keycloakAuthURL);
//
//        if (keycloakAuthURL.isEmpty()) {
//            keycloakAuthURL = "http://localhost:8080/auth";
//        }
//        keycloakClient = KeycloakBuilder.builder()
//                .serverUrl(keycloakAuthURL) //
//                .realm(CommonString.getInstance().getMastRealm())//
//                .username(CommonString.getInstance().getMasterAdminUsrname()) //
//                .password(CommonString.getInstance().getMasterAdminPassword()) //
//                .clientId(CommonString.getInstance().getAdminClientId())
//                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
//                .build(); 
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
