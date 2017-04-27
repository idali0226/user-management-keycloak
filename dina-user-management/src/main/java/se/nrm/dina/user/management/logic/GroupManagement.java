/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;
import javax.ejb.Stateless;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.GroupRepresentation;

/**
 *
 * @author idali
 */
@Stateless
public class GroupManagement implements Serializable {
     
    private static final String MASTER_REALM = "master";
    private static final String DINA_REALM = "dina";
    private static final String ADMIN_REALM = "admin-cli";
    private static final String URL = "http://localhost:8080/auth";

    private static final String MASTER_ADMIN_USERNAME = "admin";
    private static final String MASTER_ADMIN_PASSWORD = "dina";
    
    private static final String REGEX = ".*/(.*)$";
    private static final String CLIENT_END_POINT = "dina-rest";
    private static final String CLIENT_COLLECTION = "user-management";
    
    private static final String ADMIN_ROLE = "admin";
    
    private Keycloak kc;
    
    public GroupManagement() {
        
    }
    
    public void addGroup(String groupName) {
        buildKeycloak();
        GroupRepresentation gr = new GroupRepresentation();
        kc.realm(DINA_REALM).groups().add(gr);
    }
    
    private void buildKeycloak() {   
        kc = KeycloakBuilder.builder()
                .serverUrl(URL) //
                .realm(MASTER_REALM)//
                .username(MASTER_ADMIN_USERNAME) //
                .password(MASTER_ADMIN_PASSWORD) //
                .clientId(ADMIN_REALM)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
                .build();
    }
}
