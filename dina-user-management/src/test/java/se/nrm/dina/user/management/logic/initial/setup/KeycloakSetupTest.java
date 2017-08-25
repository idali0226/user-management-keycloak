/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.initial.setup;
 
import org.junit.After; 
import org.junit.Before; 
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmsResource; 
import org.mockito.Mock; 
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import se.nrm.dina.user.management.logic.RealmManagement;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
@RunWith(MockitoJUnitRunner.class)
public class KeycloakSetupTest {
    
    private KeycloakSetup instance;
    
    @Mock
    private RealmManagement realm;
      
    public KeycloakSetupTest() {
    }
 
    @Before
    public void setUp() { 
    }
    
    @After
    public void tearDown() {
        instance = null;
    }
     

    /**
     * Test of init method, of class KeycloakSetup.
     */
    @Test
    public void testInitRealmExists() {
        System.out.println("init"); 
    
        instance = new KeycloakSetup(realm); 
        when(realm.isRealmExist()).thenReturn(true); 
        instance.init();     
        verify(realm, times(0)).createRealm();
    }
    
        /**
    * Test of init method, of class KeycloakSetup.
     */
    @Test
    public void testInitRealmNotExists() {
        System.out.println("init"); 
    
        instance = new KeycloakSetup(realm); 
        when(realm.isRealmExist()).thenReturn(false); 
        instance.init();     
        verify(realm, times(1)).createRealm();
        verify(realm, times(1)).createRealmRoles();
        
        verify(realm, times(1)).createRealmClient(  CommonString.getInstance().getDinaRestClientId(), 
                                                    CommonString.getInstance().getDinaRestClientName(), 
                                                    CommonString.getInstance().getDinaRestClientDescription(), true);
        
        verify(realm, times(1)).createRealmClient(  CommonString.getInstance().getUserManagementClientId(),
                                                    CommonString.getInstance().getUserManagementClientName(),
                                                    CommonString.getInstance().getUserManagementClientDescription(), false); 
        verify(realm, times(1)).createClientRoles();
        verify(realm, times(1)).createRealmInitialUsers();
        
    }
}
