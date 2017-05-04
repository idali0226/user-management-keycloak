/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable; 
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.GroupRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */ 
public class GroupManagement implements Serializable {
  
    private final Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(null);
    private Keycloak keycloakClient;
    
    public GroupManagement() {
        
    }
    
    public void addGroup(String realm, String groupJson) {
        logger.info("addGroup : {}", groupJson);
        buildRealm();
        
        GroupRepresentation gr = new GroupRepresentation();
        
        try (JsonReader jsonReader = Json.createReader(new StringReader(groupJson))) {
            JsonObject jsonObject = jsonReader.readObject();
            
        }


        
        
        keycloakClient.realm(realm).groups().add(gr);
        
        keycloakClient.close();
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
