/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;   
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import javax.inject.Inject; 
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;  
import org.keycloak.admin.client.resource.ClientResource; 
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;  
import se.nrm.dina.user.management.json.JsonConverter;   

/**
 *
 * @author idali
 */ 
@Slf4j
public class ClientManagement implements Serializable { 
 
    @Inject
    public JsonConverter json;
 
    @Inject
    private RealmManagement realmManagement;
 
    public ClientManagement() {
    }
 
    public JsonObject getClientById(String id) {
        log.info("getClientById");

        ClientResource clientResource = realmManagement.getDinaRealmResource().clients().get(id);
         
        ClientRepresentation clientRepresentation = clientResource.toRepresentation();
        List<RoleRepresentation> roleRepresentations = clientResource.roles().list();
        
        return json.converterClient(clientRepresentation, roleRepresentations);
    }
    
    public JsonObject getAllTheClients() {
 
        RealmResource dinaRealmResource = realmManagement.getDinaRealmResource();
        List<ClientRepresentation> clientsRepresetation = dinaRealmResource.clients().findAll();
        
        Map<ClientRepresentation, List<RoleRepresentation>> map = new HashMap();
        clientsRepresetation.stream() 
                .filter(c -> !c.getName().contains("client")) 
                .forEach(c -> {
                    RolesResource rolesResource = dinaRealmResource.clients().get(c.getId()).roles(); 
                    map.put(c, rolesResource.list());
                });
          
        return json.converterClients(map); 
    }   
}
