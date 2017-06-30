/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable; 
import java.util.List; 
import javax.inject.Inject;
import javax.json.JsonObject;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource; 
import org.keycloak.admin.client.resource.RolesResource;  
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
public class RoleManagement implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());  
    
    private Keycloak keycloakClient;
    private RoleRepresentation roleRepresentation;
    
    @Inject
    public JsonConverter json;
        
    public RoleManagement() {
        
    }
    
    public JsonObject getRoleById(String id) {
        buildRealm();
         
        ClientsResource clientsResource = keycloakClient.realm(CommonString.getInstance().getDinaRealm()).clients(); 
         
        roleRepresentation = null;
        List<ClientRepresentation> clientRepresentations = clientsResource.findAll();
        clientRepresentations.stream()
                                .forEach(c -> { 
                                    ClientResource clientResource = clientsResource.get(c.getId());
                                    clientResource.roles().list().stream()
                                            .forEach(r -> {
                                                logger.info("id : {} --- {}", r.getId(), r.getName());
                                                if(r.getId().equals(id)) {
                                                    logger.info("find");
                                                    roleRepresentation = r;
                                                }
                                            });
                                });
        
//        List<RolesResource> roleResource =
                      
                                                                
                                                                 
                                                                
                
                
//                                                                .map(r -> r.get(id))
//                                                                .filter(r -> r != null)
//                                                                .findFirst()
//                                                                .get();
 
                       
        
        keycloakClient.close();
        
        return roleRepresentation == null ? null : json.converterRole(roleRepresentation);
    }
    
    public JsonObject getAllRealmRoles(String realm) {
        buildRealm();
        
        RolesResource rolesResource = keycloakClient.realm(realm).roles();
         
        keycloakClient.close(); 
        return json.converterRoles(rolesResource);
    }
    
    
    
    public JsonObject getClientRolesByClientId(String realm, String clientId) {
        
        logger.info("getClientRolesByClientId : {} -- {}", realm, clientId);
        
        
        buildRealm();
          
        RolesResource rolesResoruce = keycloakClient.realm(realm).clients().get(clientId).roles();
       
        keycloakClient.close(); 
        return json.converterRoles(rolesResoruce);
    }
    
    
    
    private void buildRealm() {   
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
