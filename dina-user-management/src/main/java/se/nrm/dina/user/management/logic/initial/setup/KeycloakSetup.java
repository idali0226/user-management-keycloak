/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.initial.setup;
  
import java.io.Serializable; 
import javax.annotation.PostConstruct;  
import javax.ejb.Singleton;
import javax.ejb.Startup; 
import javax.inject.Inject;   
import lombok.extern.slf4j.Slf4j;  
import se.nrm.dina.user.management.logic.RealmManagement; 
import se.nrm.dina.user.management.utils.CommonString;  

/**
 *
 * @author idali
 * 
 * KeycloakSetup setup initial data for Keycloak to run.
 * 
 * The following data are set during the setup process:
 * 
 * <ul>
 *  <li>Create realm</li>
 *  <li>Set password policies</li>
 *  <li>Set mail server</li>
 *  <li>Create realm roles</li>
 *  <li>Create rest endpoint client</li>
 *  <li>Create roles for rest endpoint client</li>
 *  <li>Create user-management client</li>
 *  <li>Create roles for user-management client</li>
 *  <li>Create initial users (Super admin, Admin and User)</li> 
 * </ul> 
 * 
 */
@Startup                            // initialize ejb at deployment time
@Singleton
@Slf4j
public class KeycloakSetup implements Serializable { 
 
    @Inject
    private RealmManagement realmManagement;
      
    public KeycloakSetup() {
    }
    
    public KeycloakSetup(RealmManagement realmManagement) {
        this.realmManagement = realmManagement;
    }
 
    /**
     * init method runs when application server starts up and user-management start to deploy
     * 
     */
    @PostConstruct
    public void init() {
        log.info("init");  
         
        if(!realmManagement.isRealmExist()) {   
            realmManagement.createRealm(); 
            realmManagement.createRealmRoles();
            realmManagement.createRealmClient(CommonString.getInstance().getDinaRestClientId(), 
                                              CommonString.getInstance().getDinaRestClientName(), 
                                              CommonString.getInstance().getDinaRestClientDescription(), true); 
            
            realmManagement.createRealmClient(CommonString.getInstance().getUserManagementClientId(),
                                              CommonString.getInstance().getUserManagementClientName(),
                                              CommonString.getInstance().getUserManagementClientDescription(), false); 
            
            realmManagement.createClientRoles();
            realmManagement.createRealmInitialUsers(); 
        } 
    }     
}
 