/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.keycloak;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;  
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder; 
import se.nrm.dina.user.management.keycloak.properties.ConfigurationProperties;
import se.nrm.dina.user.management.utils.CommonString; 

/**
 *
 * @author idali
 * 
 * KeycloakClientProducer produce Keycloak client which can be injected into anywhere within the application
 * 
 */
@ApplicationScoped
@Startup 
@Slf4j
public class KeycloakClientProducer {
    
    
    private Keycloak keycloakClient;  
  
    @Inject
    private ConfigurationProperties config;
    
    public KeycloakClientProducer() {
        
    }
    
    public KeycloakClientProducer(ConfigurationProperties config, Keycloak keycloakClient) { 
        this.config = config;
        this.keycloakClient = keycloakClient;
    }
     
    /**
     * init method runs when the application server starts up and user-management application start to deploy
     */
    @PostConstruct
    public void init() { 
        log.info("init" );   
        
        keycloakClient = KeycloakBuilder.builder()
                                        .serverUrl(config.getKeycloakAuthURL())
                                        .realm(CommonString.getInstance().getMastRealm())
                                        .username(CommonString.getInstance().getMasterAdminUsrname())
                                        .password(CommonString.getInstance().getMasterAdminPassword())
                                        .clientId(CommonString.getInstance().getAdminClientId())
                                        .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                                        .build();   
    }
    
    /**
     * 
     * Produce CDI KeycloakClient
     * 
     * @return Keycloak
     */
    @Produces
    @KeycloakClient
    public Keycloak getKeycloakClient() {
        return keycloakClient;
    }
  
    /**
     * Close KeycloakClient when bean is destroyed
     */
    @PreDestroy
    public void preDestroy() {
        log.info("preDestroy - keyclokClient is closed");
        keycloakClient.close();
    }
}
