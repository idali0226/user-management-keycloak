/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.keycloak;

import org.junit.After; 
import org.junit.Before; 
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak; 
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import se.nrm.dina.user.management.keycloak.properties.ConfigurationProperties;

/**
 *
 * @author idali
 */
@RunWith(MockitoJUnitRunner.class)
public class KeycloakClientProducerTest {
     
    private KeycloakClientProducer instance; 
    
    @Mock
    private ConfigurationProperties config;
     
    @Mock
    private Keycloak keycloakClient;
     
      
    public KeycloakClientProducerTest() {
    }
  
    @Before
    public void setUp() {
        System.out.println("config : " + config);
         
        when(config.getKeycloakAuthURL()).thenReturn("http://localhost:8080/auth"); 
        instance = new KeycloakClientProducer(config, keycloakClient); 
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class KeycloakClientProducer.
     */
    @Test
    public void testInit() {
        System.out.println("init");
    
        instance.init();  
        assertNotNull(instance.getKeycloakClient());
        verify(config).getKeycloakAuthURL();
    }

    /**
     * Test of getKeycloakClient method, of class KeycloakClientProducer.
     */
    @Test
    public void testGetKeycloakClient() {
        System.out.println("getKeycloakClient");
  
        Keycloak result = instance.getKeycloakClient();
        assertNotNull(result); 
    }

    /**
     * Test of preDestroy method, of class KeycloakClientProducer.
     */
    @Test
    public void testPreDestroy() {
        System.out.println("preDestroy");
      
//        instance = new KeycloakClientProducer(config, keycloakClient);
        instance.preDestroy();  
        verify(keycloakClient).close();
    }
    
}
