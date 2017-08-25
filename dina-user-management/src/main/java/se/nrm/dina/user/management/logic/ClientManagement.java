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
import se.nrm.dina.user.management.logic.helpers.KeycloakHelper;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */ 
@Slf4j
public class ClientManagement implements Serializable { 
 
    @Inject
    public JsonConverter json;

//    @Inject
//    @KeycloakClient
//    private Keycloak keycloakClient;
    
    @Inject
    private RealmManagement realmManagement;

//    @Inject
//    public ConfigurationProperties config;
    public ClientManagement() {
    }

//    public void createClientRoles(String clientId) {
//        KeycloakHelper helper = new KeycloakHelper();
//        helper.setupBasicRoleMap().entrySet()
//                                    .stream()
//                                    .forEach(r -> {
//                                        createClientRole(CommonString.getInstance().getDinaRestClientId(), r.getKey(), r.getValue());
//                                        createClientRole(CommonString.getInstance().getUserManagementClientId(), r.getKey(), r.getValue());
//                                    });
//    }
    
//    public void createClientRole(String clientId, String role, String description) { 
//        
//        log.info("createClientRole");
//        
//        RealmResource dinaRealmResource = realmManagement.getDinaRealmResource();
//        RoleRepresentation clientRoleRepresentation = new RoleRepresentation();
//        clientRoleRepresentation.setName(role);
//        clientRoleRepresentation.setDescription(description);
//        
//        clientRoleRepresentation.setClientRole(true);
//        
//        ClientRepresentation clientRepresentation = dinaRealmResource.clients().findByClientId(clientId).get(0);
//        log.info("client name : {} -- {}", clientRepresentation.getName(), clientRepresentation.getId());
//        
//        dinaRealmResource.clients().get(clientRepresentation.getId()).roles().create(clientRoleRepresentation);
//        
////        dinaRealmResource.clients().findByClientId(clientId)
////                .forEach(clientRepresentation ->
////                    dinaRealmResource.clients().get(clientRepresentation.getId()).roles().create(clientRoleRepresentation)
////                ); 
//    }
//    
    
    

//    public void createClient(String clientId) {
//        ClientRepresentation clientRepresentation = new ClientRepresentation();
//        clientRepresentation.setClientId(CommonString.getInstance().getUserManagementClientId());
//        clientRepresentation.setName(CommonString.getInstance().getUserManagementClientName());
//        clientRepresentation.setDescription("Management user account in keycloak");
//        clientRepresentation.setEnabled(Boolean.TRUE);
//        clientRepresentation.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
//        clientRepresentation.setDirectAccessGrantsEnabled(Boolean.FALSE);
//        clientRepresentation.setBearerOnly(Boolean.TRUE);
//
//        realmResource.clients().create(clientRepresentation);
//
//    }

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
