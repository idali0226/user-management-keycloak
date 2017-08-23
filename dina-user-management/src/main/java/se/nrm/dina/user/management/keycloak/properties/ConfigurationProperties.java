/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.keycloak.properties;
 
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

/**
 *
 * @author idali
 * 
 * This class injects configuration properties from yml file when the application is initialized.
 */
@ApplicationScoped 
@Slf4j
public class ConfigurationProperties {
    
    private final static String CONFIG_INITIALLISING_ERROR = "config property not initialised";
 
    private String keycloakAuthURL;
    private String dinaRealm;
    private String emailHost;
    private String emailPort;
    private String emailUsername;
    private String emailPassword;
    private String emailFrom;
    
    private String superAdminUsername;
    private String superAdminPassword;
    private String superAdminFirstname;
    private String superAdminLastname;
    
    private String adminUsername;
    private String adminPassword;
    private String adminFirstname;
    private String adminLastname;
    
    private String userUsername;
    private String userPassword;
    private String userFirstname;
    private String userLastname;
    
    private String passwordPolicies;
    
    public ConfigurationProperties() {
        
    }

    @Inject
    public ConfigurationProperties(@ConfigurationValue("swarm.keycloak.url") String keycloakAuthURL,
                                   @ConfigurationValue("swarm.realm.name") String  dinaRealm,
                                   @ConfigurationValue("swarm.email.host") String emailHost,
                                   @ConfigurationValue("swarm.email.port") String emailPort,
                                   @ConfigurationValue("swarm.email.username") String emailUsername,
                                   @ConfigurationValue("swarm.email.password") String emailPassword,
                                   @ConfigurationValue("swarm.email.from") String emailFrom,
                                   @ConfigurationValue("swarm.user.superadmin.username") String superAdminUsername,
                                   @ConfigurationValue("swarm.user.superadmin.password") String superAdminPassword,
                                   @ConfigurationValue("swarm.user.superadmin.firstname") String superAdminFirstname,
                                   @ConfigurationValue("swarm.user.superadmin.lastname") String superAdminLastname,
                                   @ConfigurationValue("swarm.user.admin.username") String adminUsername,
                                   @ConfigurationValue("swarm.user.admin.password") String adminPassword,
                                   @ConfigurationValue("swarm.user.admin.firstname") String adminFirstname,
                                   @ConfigurationValue("swarm.user.admin.lastname") String adminLastname,
                                   @ConfigurationValue("swarm.user.user.username") String userUsername,
                                   @ConfigurationValue("swarm.user.user.password") String userPassword,
                                   @ConfigurationValue("swarm.user.user.firstname") String userFirstname,
                                   @ConfigurationValue("swarm.user.user.lastname") String userLastname,
                                   @ConfigurationValue("swarm.realm.passwordpolicies") String passwordPolicies) {
        this.keycloakAuthURL = keycloakAuthURL; 
        this.dinaRealm = dinaRealm;
        this.emailHost = emailHost;
        this.emailPort = emailPort;
        this.emailUsername = emailUsername;
        this.emailPassword = emailPassword;
        this.emailFrom = emailFrom;
        this.superAdminUsername = superAdminUsername;
        this.superAdminPassword = superAdminPassword;
        this.superAdminFirstname = superAdminFirstname;
        this.superAdminLastname = superAdminLastname;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminFirstname = adminFirstname;
        this.adminLastname = adminLastname;
        this.userUsername = userUsername;
        this.userPassword = userPassword;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.passwordPolicies = passwordPolicies;
        
        log.info("test injection : {}", keycloakAuthURL);
    }
 
    /**
     * This method returns email host property
     * 
     * @return String
     */
    public String getEmailHost() {
        if(emailHost == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return emailHost;
    }
    
    /**
     * This method returns email port property
     * 
     * @return String
     */
    public String getEmilPort() {
        if(emailPort == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return emailPort;
    }
    
    /**
     * This method returns email from property
     * 
     * @return String
     */
    public String getEmailFrom() {
        if(emailFrom == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return emailFrom;
    }
    
    /**
     * This method returns email's username property
     * 
     * @return String
     */
    public String getEmailUsername() {
        if(emailUsername == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return emailUsername;
    }
    
    /**
     * This method returns email's password property
     * 
     * @return String
     */
    public String getEmailPassword() {
        if(emailPassword == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return emailPassword;
    }
    
    /**
     * This method returns super admin's username property
     * 
     * @return String
     */
    public String getSuperAdminUsername() {
        if(superAdminUsername == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return superAdminUsername;
    }
    
    /**
     * This method returns super admin's password property
     * 
     * @return String
     */
    public String getSuperAdminPassword() {
        if(superAdminPassword == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return superAdminPassword;
    }
    
    /**
     * This method returns super admin firstname property
     * 
     * @return String
     */
    public String getSuperAdminFirstname() {
        if(superAdminFirstname == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return superAdminFirstname;
    }
    
    /**
     * This method returns super admin lastname property
     * 
     * @return String
     */
    public String getSuperAdminLastname() {
        if(superAdminLastname == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return superAdminLastname;
    }
    
    /**
     * This method returns admin's username property
     * 
     * @return String
     */
    public String getAdminUsername() {
        if(adminUsername == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return adminUsername;
    }
    
    /**
     * This method returns admin's password property
     * 
     * @return String
     */
    public String getAdminPassword() {
        if(adminPassword == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return adminPassword;
    }
    
    /**
     * This method returns admin's firstname property
     * 
     * @return String
     */
    public String getAdminFirstname() {
        if(adminFirstname == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return adminFirstname;
    }
    
    /**
     * This method returns admin's lastname property
     * 
     * @return String
     */
    public String getAdminLastname() {
        if(adminLastname == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return adminLastname;
    }
    
    /**
     * This method returns user's username property
     * 
     * @return String
     */
    public String getUserUsername() {
        if(userUsername == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return userUsername;
    }
    
    /**
     * This method returns user's password property
     * 
     * @return String
     */
    public String getUserPassword() {
        if(userPassword == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return userPassword;
    }
    
    /**
     * This method returns user's firstname property
     * 
     * @return String
     */
    public String getUserFirstname() {
        if(userFirstname == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return userFirstname;
    }
    
    /**
     * This method returns user's lastname property
     * 
     * @return String
     */
    public String getUserLastname() {
        if(userLastname == null) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return userLastname;
    }
    
    /**
     * This method returns password policies property
     * 
     * @return String
     */
    public String getPasswordPolicies() {
        if(null == passwordPolicies) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return passwordPolicies;
    }
      
    /**
     * This method returns keycloak auth URL property
     * 
     * @return String
     */
    public String getKeycloakAuthURL() {
        if(null == keycloakAuthURL) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return keycloakAuthURL;
    }
    
    /**
     * This method returns realm name property
     * 
     * @return String
     */
    public String getRealm() {
        if(null == dinaRealm) {
            throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
        }
        return dinaRealm;
    } 
}
