/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;    
import javax.inject.Inject;
import javax.json.JsonObject; 
import lombok.extern.slf4j.Slf4j; 
import org.keycloak.admin.client.Keycloak;  
import org.keycloak.admin.client.resource.ClientsResource; 
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;   
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.keycloak.KeycloakClient; 
import se.nrm.dina.user.management.keycloak.properties.ConfigurationProperties;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
@Slf4j
public class RoleManagement implements Serializable { 
     
    @Inject
    public JsonConverter json;
    
    @Inject
    @KeycloakClient
    private Keycloak keycloakClient;
    
//    @Inject
//    @KeycloakClient
//    private String dinaRealm;
    
    @Inject
    public ConfigurationProperties config;
        
    public RoleManagement() {
        
    } 
    
    public JsonObject getRoleById(String id) {   
        log.info("getRoleById");
        
        String realm = config.getRealm();
        
        RoleRepresentation roleRepresentation = keycloakClient.realm(realm).rolesById().getRole(id);
        
        String roleBelongTo = realm;
        if (roleRepresentation.getClientRole()) {
            
            ClientsResource clientsResource = keycloakClient.realm(realm).clients(); 
            ClientRepresentation clientRepresentation = keycloakClient.realm(realm).clients().findByClientId(CommonString.getInstance().getDinaRestClientId()).get(0);

            if(isClientByRole(clientsResource, clientRepresentation.getId(), id)) {
                roleBelongTo = clientRepresentation.getName();
            } else {
                clientRepresentation = keycloakClient.realm(realm).clients().findByClientId(CommonString.getInstance().getUserManagementClientId()).get(0);
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
}
