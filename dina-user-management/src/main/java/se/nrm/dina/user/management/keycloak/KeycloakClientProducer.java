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
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;
import se.nrm.dina.user.management.utils.CommonString; 

/**
 *
 * @author idali
 */
@ApplicationScoped
@Startup 
@Slf4j
public class KeycloakClientProducer {
    
    private Keycloak keycloakClient;
    
    @Inject
    @ConfigurationValue("swarm.keycloak.url")
    private String keycloakAuthURL;
    
    @Inject
    @ConfigurationValue("swarm.realm.name")
    private String dinaRealm;
    
        
    @PostConstruct
    public void init() {
        
        log.info("init : {}", keycloakAuthURL );
        
        keycloakClient = KeycloakBuilder.builder()
                                        .serverUrl(keycloakAuthURL)
                                        .realm(CommonString.getInstance().getMastRealm())
                                        .username(CommonString.getInstance().getMasterAdminUsrname())
                                        .password(CommonString.getInstance().getMasterAdminPassword())
                                        .clientId(CommonString.getInstance().getAdminClientId())
                                        .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                                        .build(); 
    }
    
    @Produces
    @KeycloakClient
    public Keycloak getKeycloakClient() {
        return keycloakClient;
    }
    
    @Produces
    @KeycloakClient
    public String getRealmName() {
        return dinaRealm;
    }
    
    @PreDestroy
    public void preDestroy() {
        log.info("preDestroy - keyclokClient is closed");
        keycloakClient.close();
    }
}
