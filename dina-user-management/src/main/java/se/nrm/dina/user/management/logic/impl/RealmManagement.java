/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.impl;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.logic.helpers.KeycloakClientHelper;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
public class RealmManagement {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());  
    
    private final Keycloak keycloak;
    private final String realmName;
    
    public RealmManagement(Keycloak keycloak, String realmName) {
        this.keycloak = keycloak;
        this.realmName = realmName;
    }
    
    public void createRealm(String passwordPolicies) {
         logger.info("createRealm"); 
         
        RealmRepresentation realmRepresenttion = new RealmRepresentation();
        realmRepresenttion.setRealm(realmName);
        realmRepresenttion.setDisplayName(realmName);
        realmRepresenttion.setSslRequired(CommonString.getInstance().getNone()); 
        realmRepresenttion.setDuplicateEmailsAllowed(false);
        
        realmRepresenttion.setEventsEnabled(true);
        realmRepresenttion.setAdminEventsDetailsEnabled(Boolean.TRUE);
        realmRepresenttion.setAdminEventsEnabled(Boolean.TRUE);
        realmRepresenttion.setEditUsernameAllowed(Boolean.TRUE);
        
        realmRepresenttion.setAccessCodeLifespanUserAction(90000); 
        
        realmRepresenttion.setSmtpServer(KeycloakClientHelper.getInstance().setupMailServer()); 
        realmRepresenttion.setPasswordPolicy(passwordPolicies);
        realmRepresenttion.setEnabled(true);
         
        keycloak.realms().create(realmRepresenttion); 
    }
}
