/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.RealmRepresentation;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
public class RealmManagement implements Serializable {

    private Keycloak keycloakClient;
    
    public RealmManagement() {
        
    }
    
    public void createRealm(String realmName) {
        RealmRepresentation rr = new RealmRepresentation();
        rr.setDuplicateEmailsAllowed(false);
        rr.setRealm("myrealm");
        keycloakClient.realms().create(rr);
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
