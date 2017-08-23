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
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.representations.idm.ClientRepresentation;  
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;   
import se.nrm.dina.user.management.json.JsonConverter; 
import se.nrm.dina.user.management.keycloak.KeycloakClient;
import se.nrm.dina.user.management.keycloak.properties.ConfigurationProperties;

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
    public JsonConverter json;
  
    public RealmManagement() {  
    }   
    
    public RealmsResource getRealmResources() {
        return keycloakClient.realms();
    }
  
    public boolean isRealmExist() { 
        return getRealmResources().findAll().stream().anyMatch(realmExist());  
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
