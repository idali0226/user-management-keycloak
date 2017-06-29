/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.keycloak.management.logic;

import java.util.HashMap;
import java.util.Map;   
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;  
import org.keycloak.representations.idm.RealmRepresentation;

/**
 *
 * @author idali
 */
public class RealmManagement {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());  
    
    private static final String MASTER_REALM = "master";
    private static final String ADMIN_CLIENT_ID = "admin-cli";
    private static final String KEYCLOAK_URL = "http://localhost:8080/auth";

    private static final String MASTER_ADMIN_USERNAME = "admin";
    private static final String MASTER_ADMIN_PASSWORD = "dina";
    
    private Keycloak keycloakClient; 
    
    
    public RealmManagement() {
        
    }
    
    public void createRealm(String realmName) {
        
        System.out.println("createRealm: " + realmName);
        buildRealm();
        
        System.out.println("realms : " + keycloakClient.realms().findAll().size());
        
        RealmRepresentation realmRepresenttion = new RealmRepresentation();
        realmRepresenttion.setRealm(realmName);
   
        realmRepresenttion.setDuplicateEmailsAllowed(false);
        
        Map<String, String> smtpServerMap = new HashMap<>();
        smtpServerMap.put("host", "mail.dina-web.net");
        smtpServerMap.put("port", "587"); 
        smtpServerMap.put("from", "dina@mail.dina-web.net"); 
        smtpServerMap.put("starttls", "true");
        smtpServerMap.put("authentication", "true");
        smtpServerMap.put("username", "dina@mail.dina-web.net");
        smtpServerMap.put("password", "D-I-N-A");
         
        realmRepresenttion.setSmtpServer(smtpServerMap);
        realmRepresenttion.setEnabled(true); 
                  
        keycloakClient.realms().create(realmRepresenttion);  
        keycloakClient.close();
    }
    
    private void buildRealm() {    
        keycloakClient = KeycloakBuilder.builder()
                                        .serverUrl(KEYCLOAK_URL) //
                                        .realm(MASTER_REALM)//
                                        .username(MASTER_ADMIN_USERNAME) //
                                        .password(MASTER_ADMIN_PASSWORD) //
                                        .clientId(ADMIN_CLIENT_ID)
                                        .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) 
                                        .build();
    } 
}
