/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.keycloak.properties;

import org.junit.After; 
import org.junit.AfterClass;
import org.junit.Before; 
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author idali
 */
public class ConfigurationPropertiesTest {
    
    private String keycloakAuthURL;
    private String realm;
    private String emailHost;
    private String emailPort;
    private String emailFrom;
    private String emailUsername;
    private String emailPassword;
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
    
    private ConfigurationProperties testInstanceWithInjection;
    private ConfigurationProperties testInstanceWithEmptyInjection;
    
    public ConfigurationPropertiesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
 
    @Before
    public void setUp() {
        
        keycloakAuthURL = "http://keycloak/auth";
        realm = "dina";
        emailHost = "mail.dina-web.net";
        emailPort = "587";
        emailFrom = "dina@mail.dina-web.net";
        emailUsername = "dina@mail.dina-web.net";
        emailPassword = "password";
        superAdminUsername = "super.admin@dina.se";
        superAdminPassword = "superAdminPassword";
        superAdminFirstname = "SuperAdmin";
        superAdminLastname = "SuperAdmin";
        adminUsername = "admin@dina.se";
        adminPassword = "admin";
        adminFirstname = "Admin";
        adminLastname = "Dina";
        userUsername = "user@dina.se";
        userPassword = "user";        
        userFirstname = "User";
        userLastname = "Dina";
        passwordPolicies = "password policies";
        
        testInstanceWithInjection = new ConfigurationProperties( keycloakAuthURL, realm, emailHost, emailPort, emailUsername, 
                                                                emailPassword, emailFrom, superAdminUsername, superAdminPassword, 
                                                                superAdminFirstname, superAdminLastname, adminUsername, 
                                                                adminPassword, adminFirstname, adminLastname, userUsername, 
                                                                userPassword, userFirstname, userLastname, passwordPolicies);
        
        testInstanceWithEmptyInjection = new ConfigurationProperties(null, null, null, null, null, null, null, null, null, null, 
                                                                     null, null, null, null, null, null, null, null, null, null);
    }
    
    @After
    public void tearDown() {
        testInstanceWithInjection = null;
        testInstanceWithEmptyInjection = null;
    }

    /**
     * Test of getEmailHost method, of class ConfigurationProperties.
     */
    @Test
    public void testGetEmailHost() {
        System.out.println("getEmailHost"); 
        
        String expResult = "mail.dina-web.net";
        String result = testInstanceWithInjection.getEmailHost();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getEmilPort method, of class ConfigurationProperties.
     */
    @Test
    public void testGetEmilPort() {
        System.out.println("getEmilPort"); 
        
        String expResult = "587";
        String result = testInstanceWithInjection.getEmilPort();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getEmailFrom method, of class ConfigurationProperties.
     */
    @Test
    public void testGetEmailFrom() {
        System.out.println("getEmailFrom"); 
        
        String expResult = "dina@mail.dina-web.net";
        String result = testInstanceWithInjection.getEmailFrom();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getEmailUsername method, of class ConfigurationProperties.
     */
    @Test
    public void testGetEmailUsername() {
        System.out.println("getEmailUsername"); 
        
        String expResult = "dina@mail.dina-web.net";
        String result = testInstanceWithInjection.getEmailUsername();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getEmailPassword method, of class ConfigurationProperties.
     */
    @Test
    public void testGetEmailPassword() {
        System.out.println("getEmailPassword"); 
        
        String expResult = "password";
        String result = testInstanceWithInjection.getEmailPassword();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getSuperAdminUsername method, of class ConfigurationProperties.
     */
    @Test
    public void testGetSuperAdminUsername() {
        System.out.println("getSuperAdminUsername");
 
        String expResult = "super.admin@dina.se";
        String result = testInstanceWithInjection.getSuperAdminUsername();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getSuperAdminPassword method, of class ConfigurationProperties.
     */
    @Test
    public void testGetSuperAdminPassword() {
        System.out.println("getSuperAdminPassword");
 
        String expResult = "superAdminPassword";
        String result = testInstanceWithInjection.getSuperAdminPassword();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getSuperAdminFirstname method, of class ConfigurationProperties.
     */
    @Test
    public void testGetSuperAdminFirstname() {
        System.out.println("getSuperAdminFirstname");
 
        String expResult = "SuperAdmin";
        String result = testInstanceWithInjection.getSuperAdminFirstname();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getSuperAdminLastname method, of class ConfigurationProperties.
     */
    @Test
    public void testGetSuperAdminLastname() {
        System.out.println("getSuperAdminLastname");
 
        String expResult = "SuperAdmin";
        String result = testInstanceWithInjection.getSuperAdminLastname();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getAdminUsername method, of class ConfigurationProperties.
     */
    @Test
    public void testGetAdminUsername() {
        System.out.println("getAdminUsername");
  
        String expResult = "admin@dina.se";
        String result = testInstanceWithInjection.getAdminUsername();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getAdminPassword method, of class ConfigurationProperties.
     */
    @Test
    public void testGetAdminPassword() {
        System.out.println("getAdminPassword");
   
        String expResult = "admin";
        String result = testInstanceWithInjection.getAdminPassword();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getAdminFirstname method, of class ConfigurationProperties.
     */
    @Test
    public void testGetAdminFirstname() {
        System.out.println("getAdminFirstname");
 
        String expResult = "Admin";
        String result = testInstanceWithInjection.getAdminFirstname();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getAdminLastname method, of class ConfigurationProperties.
     */
    @Test
    public void testGetAdminLastname() {
        System.out.println("getAdminLastname");
       
        String expResult = "Dina";
        String result = testInstanceWithInjection.getAdminLastname();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getUserUsername method, of class ConfigurationProperties.
     */
    @Test
    public void testGetUserUsername() {
        System.out.println("getUserUsername");
 
        String expResult = "user@dina.se";
        String result = testInstanceWithInjection.getUserUsername();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getUserPassword method, of class ConfigurationProperties.
     */
    @Test
    public void testGetUserPassword() {
        System.out.println("getUserPassword");
 
        String expResult = "user";
        String result = testInstanceWithInjection.getUserPassword();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getUserFirstname method, of class ConfigurationProperties.
     */
    @Test
    public void testGetUserFirstname() {
        System.out.println("getUserFirstname");
  
        String expResult = "User";
        String result = testInstanceWithInjection.getUserFirstname();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getUserLastname method, of class ConfigurationProperties.
     */
    @Test
    public void testGetUserLastname() {
        System.out.println("getUserLastname");
 
        String expResult = "Dina";
        String result = testInstanceWithInjection.getUserLastname();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getPasswordPolicies method, of class ConfigurationProperties.
     */
    @Test
    public void testGetPasswordPolicies() {
        System.out.println("getPasswordPolicies");
      
        String expResult = "password policies";
        String result = testInstanceWithInjection.getPasswordPolicies();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getKeycloakAuthURL method, of class ConfigurationProperties.
     */
    @Test
    public void testGetKeycloakAuthURL() {
        System.out.println("getKeycloakAuthURL");
 
        String expResult = "http://keycloak/auth";
        String result = testInstanceWithInjection.getKeycloakAuthURL();
        assertEquals(expResult, result); 
    }

    /**
     * Test of getRealm method, of class ConfigurationProperties.
     */
    @Test
    public void testGetRealm() {
        System.out.println("getRealm"); 
        
        String expResult = "dina";
        String result = testInstanceWithInjection.getRealm();
        assertEquals(expResult, result); 
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetEmailHostFailure() {
        System.out.println("testGetEmailHostFailure");
         
        testInstanceWithEmptyInjection.getEmailHost(); 
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetEmailPortFailure() {
        System.out.println("testGetEmailPortFailure");
         
        testInstanceWithEmptyInjection.getEmilPort();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetEmailUsernameFailure() {
        System.out.println("testGetEmailUsernameFailure");
         
        testInstanceWithEmptyInjection.getEmailUsername();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetEmailPasswordFailure() {
        System.out.println("testGetEmailHostFailure");
         
        testInstanceWithEmptyInjection.getEmailPassword();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetEmailFromFailure() {
        System.out.println("testGetEmailFromFailure");
         
        testInstanceWithEmptyInjection.getEmailFrom();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetKeycloakAuthURLFailure() {
        System.out.println("testGetKeycloakAuthURLFailure");
         
        testInstanceWithEmptyInjection.getKeycloakAuthURL();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetRealmFailure() {
        System.out.println("testGetRealmFailure");
         
        testInstanceWithEmptyInjection.getRealm();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetPasswordPoliciesFailure() {
        System.out.println("testGetPasswordPoliciesFailure");
         
        testInstanceWithEmptyInjection.getPasswordPolicies();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetSuperAdminUsernameFailure() {
        System.out.println("testGetSuperAdminUsernameFailure");
         
        testInstanceWithEmptyInjection.getSuperAdminUsername();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetSuperAdminPasswordFailure() {
        System.out.println("testGetSuperAdminPasswordFailure");
         
        testInstanceWithEmptyInjection.getSuperAdminPassword();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetSuperAdminFirstnameFailure() {
        System.out.println("testGetSuperAdminFirstnameFailure");
         
        testInstanceWithEmptyInjection.getSuperAdminFirstname();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetSuperAdminLastnameFailure() {
        System.out.println("testGetSuperAdminLastnameFailure");
         
        testInstanceWithEmptyInjection.getSuperAdminLastname();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetAdminUsernameFailure() {
        System.out.println("testGetAdminUsernameFailure");
         
        testInstanceWithEmptyInjection.getAdminUsername();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetAdminPasswordFailure() {
        System.out.println("testGetAdminPasswordFailure");
         
        testInstanceWithEmptyInjection.getAdminPassword();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetAdminFirstnameFailure() {
        System.out.println("testGetAdminFirstnameFailure");
         
        testInstanceWithEmptyInjection.getAdminFirstname();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetAdminLastnameFailure() {
        System.out.println("testGetAdminLastnameFailure");
         
        testInstanceWithEmptyInjection.getAdminLastname();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetUserUsernameFailure() {
        System.out.println("testGetUserUsernameFailure");
         
        testInstanceWithEmptyInjection.getUserUsername();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetUserPasswordFailure() {
        System.out.println("testGetUserPasswordFailure");
         
        testInstanceWithEmptyInjection.getUserPassword();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetUserFirstnameFailure() {
        System.out.println("testGetUserFirstnameFailure");
         
        testInstanceWithEmptyInjection.getUserFirstname();
    }
    
    @Test (expected = RuntimeException.class)
    public void testGetUserLastnameFailure() {
        System.out.println("testGetUserLastnameFailure");
         
        testInstanceWithEmptyInjection.getUserLastname();
    }
}
