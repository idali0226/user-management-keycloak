/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
public class KeycloakClientHelper {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
        
    private final String REALM_NAME;
    private final String ENV_VAR_REALM_NAME = "REALM_NAME";
    private final String ENV_KEYCLOAK_URI = "KEYCLOAK_URI";
    
    private final String MAIL_HOST = "MAIL_HOST";
    private final String MAIL_PORT = "MAIL_PORT";
    private final String MAIL_USERNAME = "MAIL_USERNAME";
    private final String MAIL_PASSWORD = "MAIL_PASSWORD";
    private final String MAIL_FROM = "MAIL_FROM"; 
    private final String SUPER_ADMIN_USERNAME = "SUPER_USERNAME";
    private final String SUPER_ADMIN_PASSWORD = "SUPER_PASSWORD";
    
    
    private static KeycloakClientHelper instance = null;
    
    public KeycloakClientHelper() {
        REALM_NAME = System.getenv(ENV_VAR_REALM_NAME);  
        logger.info("realmName : {}", REALM_NAME);
    }
    
    public static synchronized KeycloakClientHelper getInstance() {
        if (instance == null) {
            instance = new KeycloakClientHelper();
        } 
        return instance;
    }
    
    public Map<String, String> buildRealmRoleMap() {  
        Map<String, String> realmRoleMap = buildCommonRoleMap();
        realmRoleMap.put(CommonString.getInstance().getDisabledUserRole(), CommonString.getInstance().getDisabledRoleDescription());
        return realmRoleMap;
    }
    
    public Map<String, String> buildCommonRoleMap() {  
        Map<String, String> commonRoleMap = new HashMap(); 
        commonRoleMap.put(CommonString.getInstance().getAdminRole(), CommonString.getInstance().getAdminRoleDescription());
        commonRoleMap.put(CommonString.getInstance().getUserRole(), CommonString.getInstance().getUserRoleDescription()); 
        return commonRoleMap;
    }
        
    public Map<String, String> setupMailServer() {
        logger.info("setupMailServer");
          
        Map<String, String> smtpServerMap = new HashMap<>();
        smtpServerMap.put(CommonString.getInstance().getHost(), System.getenv(MAIL_HOST));
        smtpServerMap.put(CommonString.getInstance().getPort(), System.getenv(MAIL_PORT));
        smtpServerMap.put(CommonString.getInstance().getFrom(), System.getenv(MAIL_FROM));
        smtpServerMap.put(CommonString.getInstance().getSSL(), Boolean.FALSE.toString());
        smtpServerMap.put(CommonString.getInstance().getStrtTTLS(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getAuth(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getUser(), System.getenv(MAIL_USERNAME));
        smtpServerMap.put(CommonString.getInstance().getPassword(), System.getenv(MAIL_PASSWORD));
   
        return smtpServerMap;
    }
    
    
    public Keycloak buildKeycloakClient() { 
        return KeycloakBuilder.builder()
                .serverUrl(System.getenv(ENV_KEYCLOAK_URI)) 
                .realm(CommonString.getInstance().getMastRealm())
                .username(CommonString.getInstance().getMasterAdminUsrname()) 
                .password(CommonString.getInstance().getMasterAdminPassword()) 
                .clientId(CommonString.getInstance().getAdminClientId())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
    }
}
