/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.initial.setup;

import javax.ejb.embeddable.EJBContainer;
import org.junit.After; 
import org.junit.Before; 
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author idali
 */
public class KeycloakSetupTest {
    
    public KeycloakSetupTest() {
    }
      
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class KeycloakSetup.
     */
    @Ignore    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        KeycloakSetup instance = (KeycloakSetup)container.getContext().lookup("java:global/classes/KeycloakSetup");
        instance.init();
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
