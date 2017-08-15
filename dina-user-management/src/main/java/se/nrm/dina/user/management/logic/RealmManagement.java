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
import javax.inject.Inject; 
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j; 
import org.keycloak.admin.client.Keycloak;  
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;  
import org.keycloak.representations.idm.RoleRepresentation;   
import se.nrm.dina.user.management.json.JsonConverter; 
import se.nrm.dina.user.management.keycloak.KeycloakClient;

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
    @KeycloakClient
    private String dinaRealm;
        
    @Inject
    public JsonConverter json;
 
    
    public RealmManagement() {  
    }   
  
    public JsonObject getRealmByRealmName(String realmName) { 
        RealmResource realmResource = keycloakClient.realm(realmName);
        List<ClientRepresentation> clientRepresentations = realmResource.clients().findAll().stream()
                                                                        .filter(filterDefaultRealmClients())
                                                                        .collect(Collectors.toList());
         
        List<RoleRepresentation> roleRepresentations = realmResource.roles().list().stream()
                                                                        .filter(filterDefaultRealmRoles())
                                                                        .collect(Collectors.toList());
         
        return json.converterRealm(realmResource.toRepresentation(), roleRepresentations, clientRepresentations);  
    }
     
    private static Predicate<ClientRepresentation> filterDefaultRealmClients() {
        return c -> c.getName().startsWith("dina") || c.getName().startsWith("user");
    }
        
    private static Predicate<RoleRepresentation> filterDefaultRealmRoles() { 
        return r -> !(r.getName().equals("uma_authorization") || r.getName().equals("offline_access") || r.getName().equals("disabled_user"));
    } 
}
