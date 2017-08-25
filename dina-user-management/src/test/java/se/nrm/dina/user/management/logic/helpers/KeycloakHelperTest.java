/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.helpers;

import com.univocity.parsers.common.CommonSettings;
import java.util.HashMap;
import java.util.Map;
import org.junit.After; 
import org.junit.Before; 
import org.junit.Test;
import static org.junit.Assert.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import se.nrm.dina.user.management.utils.AccountStatus;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
public class KeycloakHelperTest {
    
    public KeycloakHelper instance;
    
    public KeycloakHelperTest() {
    }
 
    @Before
    public void setUp() {
        instance = new KeycloakHelper();
    }
    
    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of buildUserRepresentation method, of class KeycloakHelper.
     */
    @Test
    public void testBuildUserRepresentation() {
        System.out.println("buildUserRepresentation");
        
        String username = "dina@nrm.se";
        String firstname = "Ida";
        String lastname = "Li";
        String purpose = "Test";
        boolean emailVerfied = false;
        Enum accountStatus = AccountStatus.Pending;
        
        instance = new KeycloakHelper();
         
        UserRepresentation result = instance.buildUserRepresentation(username, firstname, lastname, purpose, emailVerfied, accountStatus);
        assertNotNull(result); 
        assertEquals(firstname, result.getFirstName());
        assertEquals(lastname, result.getLastName());
        assertEquals(username, result.getUsername());
        assertEquals("Email should be same as username", username, result.getEmail());
        assertEquals(purpose, result.getAttributes().get(CommonString.getInstance().getPurpose()).get(0));
        assertEquals(accountStatus.name(), result.getAttributes().get(CommonString.getInstance().getStatus()).get(0)); 
        assertFalse("Email should not be verified", result.isEmailVerified()); 
        assertTrue("User representation should be enabled", result.isEnabled());
    }

    /**
     * Test of buildClientRepresentation method, of class KeycloakHelper.
     */
    @Test
    public void testBuildClientRepresentationWithNoDirectGrantsEnabled() {
        System.out.println("buildClientRepresentation");
        
        String clientId = "dina";
        String clientName = "dina_client";
        String clientDescription = "Dina client";
        boolean directAccessGrantsEnable = false;
   
        instance = new KeycloakHelper(); 
        ClientRepresentation result = instance.buildClientRepresentation(clientId, clientName, clientDescription, directAccessGrantsEnable);
        assertNotNull(result); 
        assertEquals(clientId, result.getClientId());
        assertEquals(clientName, result.getName());
        assertEquals(clientDescription, result.getDescription());
        assertEquals(CommonString.getInstance().getOpenIdConnectionProtocol(), result.getProtocol());
        assertEquals(directAccessGrantsEnable, result.isDirectAccessGrantsEnabled());
        assertTrue(result.isBearerOnly());
        assertTrue(result.isEnabled());
    }
    
    @Test
    public void testBuildClientRepresentationWithDirectGrantsEnabled() {
        System.out.println("buildClientRepresentation");
        
        String clientId = "dina";
        String clientName = "dina_client";
        String clientDescription = "Dina client";
        boolean directAccessGrantsEnable = true;
   
        instance = new KeycloakHelper(); 
        ClientRepresentation result = instance.buildClientRepresentation(clientId, clientName, clientDescription, directAccessGrantsEnable);
        assertNotNull(result); 
        assertEquals(clientId, result.getClientId());
        assertEquals(clientName, result.getName());
        assertEquals(clientDescription, result.getDescription());
        assertEquals(CommonString.getInstance().getOpenIdConnectionProtocol(), result.getProtocol());
        assertEquals(directAccessGrantsEnable, result.isDirectAccessGrantsEnabled());
        assertNull(result.isBearerOnly());
        assertTrue(result.isEnabled());
    }

    /**
     * Test of buildProtocolMapper method, of class KeycloakHelper.
     */
    @Test
    public void testBuildProtocolMapper() {
        System.out.println("buildProtocolMapper");
        
        String name = "realm_role";
        String consentText = "${realmRole}";
        String protocol = "openid-connect";
        String protocolMapper = "oidc-usermodel-realm-role-mapper";
        String attribute = "realm_role";
        String claimName = "realm_role";
        
        Map<String, String> configMap = new HashMap();
        configMap.put(attribute, claimName);
                           
        instance = new KeycloakHelper();
        ProtocolMapperRepresentation result = instance.buildProtocolMapper(name, consentText, protocol, protocolMapper, configMap);
        assertNotNull(result); 
        assertEquals(result.getName(), name);
        assertEquals(result.getConsentText(), consentText);
        assertEquals(result.getProtocol(), protocol);
        assertEquals(result.getProtocolMapper(), protocolMapper);
        assertTrue(result.isConsentRequired());
        assertNotNull(result.getConfig());
        assertEquals(result.getConfig().keySet().size(), 1);
        assertEquals(result.getConfig().values().size(), 1);
        assertEquals(result.getConfig().get(attribute), claimName);
    }

    /**
     * Test of buildProtocolMap method, of class KeycloakHelper.
     */
    @Test
    public void testBuildProtocolMap() {
        System.out.println("buildProtocolMap");
        
        String attribute = "realm_role";
        String claimName = "realm_role";
       
        instance = new KeycloakHelper(); 
        Map<String, String> result = instance.buildProtocolMap(attribute, claimName);
        assertNotNull(result);
        assertEquals(result.keySet().size(), 7);
        assertEquals(result.values().size(), 7);
         
        assertEquals(claimName, result.get(CommonString.getInstance().getClientName()));
        assertEquals(attribute, result.get(CommonString.getInstance().getUserAttribute()));
        assertEquals("true", result.get(CommonString.getInstance().getUserInfoTokenClaim()));
        assertEquals("true", result.get(CommonString.getInstance().getIdTokenClaim()));   
        assertEquals("true", result.get(CommonString.getInstance().getAccessTokenClaim()));   
        assertEquals(CommonString.getInstance().getStringDataFormat(), result.get(CommonString.getInstance().getJsonTypeLabel())); 
    }

    /**
     * Test of buildCredentialRepresentation method, of class KeycloakHelper.
     */
    @Test
    public void testBuildCredentialRepresentation() {
        System.out.println("buildCredentialRepresentation");
        
        String password = "password";
        boolean isTemporary = false;

        instance = new KeycloakHelper(); 
        CredentialRepresentation result = instance.buildCredentialRepresentation(password, isTemporary);
        assertNotNull(result); 
        assertEquals(result.getValue(), password);
        assertEquals(result.getType(), CredentialRepresentation.PASSWORD);
        assertFalse(result.isTemporary());
    }

    /**
     * Test of setupBasicRoleMap method, of class KeycloakHelper.
     */
    @Test
    public void testSetupBasicRoleMap() {
        System.out.println("setupBasicRoleMap");
    
        instance = new KeycloakHelper(); 
        
        Map<String, String> result = instance.setupBasicRoleMap();
        assertNotNull(result); 
        assertEquals(result.size(), 2);
          
        assertTrue(result.containsKey(CommonString.getInstance().getAdminRole()));
        assertTrue(result.containsKey(CommonString.getInstance().getUserRole()));
         
        assertTrue(result.containsValue(CommonString.getInstance().getAdminRoleDescription()));
        assertTrue(result.containsValue(CommonString.getInstance().getUserRoleDescription()));
        
        assertEquals(result.get(CommonString.getInstance().getAdminRole()), CommonString.getInstance().getAdminRoleDescription());
        assertEquals(result.get(CommonString.getInstance().getUserRole()), CommonString.getInstance().getUserRoleDescription());
    }

    /**
     * Test of setupRealmRoleMap method, of class KeycloakHelper.
     */
    @Test
    public void testSetupRealmRoleMap() {
        System.out.println("setupRealmRoleMap");

        instance = new KeycloakHelper(); 
        Map<String, String> result = instance.setupRealmRoleMap();
        assertNotNull(result); 
        assertEquals(result.size(), 4);
        
        assertTrue(result.containsKey(CommonString.getInstance().getSuperAdminRole()));
        assertTrue(result.containsKey(CommonString.getInstance().getDisabledUserRole()));
        assertTrue(result.containsKey(CommonString.getInstance().getAdminRole()));
        assertTrue(result.containsKey(CommonString.getInstance().getUserRole()));
        
        assertTrue(result.containsValue(CommonString.getInstance().getSuperAdminRole()));
        assertTrue(result.containsValue(CommonString.getInstance().getDisabledRoleDescription()));
        assertTrue(result.containsValue(CommonString.getInstance().getAdminRoleDescription()));
        assertTrue(result.containsValue(CommonString.getInstance().getUserRoleDescription()));
        
        assertEquals(result.get(CommonString.getInstance().getSuperAdminRole()), CommonString.getInstance().getSuperAdminRole());
        assertEquals(result.get(CommonString.getInstance().getDisabledUserRole()), CommonString.getInstance().getDisabledRoleDescription());
        assertEquals(result.get(CommonString.getInstance().getAdminRole()), CommonString.getInstance().getAdminRoleDescription());
        assertEquals(result.get(CommonString.getInstance().getUserRole()), CommonString.getInstance().getUserRoleDescription());

        
    }

    /**
     * Test of buildSMTPMailMap method, of class KeycloakHelper.
     */
    @Test
    public void testBuildSMTPMailMap() {
        System.out.println("buildSMTPMailMap");
        
        String emailHost = "mail.dina.net";
        String emailPort = "123";
        String emailFrom = "dina@dina.se";
        String emailUsername = "dina";
        String emailPassword = "password";
 
        instance = new KeycloakHelper(); 
         
        Map<String, String> result = instance.buildSMTPMailMap(emailHost, emailPort, emailFrom, emailUsername, emailPassword);
        assertNotNull(result); 
        
        assertEquals(result.size(), 8);
        
        assertTrue(result.containsKey(CommonString.getInstance().getHost()));
        assertTrue(result.containsKey(CommonString.getInstance().getPort()));
        assertTrue(result.containsKey(CommonString.getInstance().getFrom()));
        assertTrue(result.containsKey(CommonString.getInstance().getSSL()));
        assertTrue(result.containsKey(CommonString.getInstance().getStrtTTLS()));
        assertTrue(result.containsKey(CommonString.getInstance().getAuth()));
        assertTrue(result.containsKey(CommonString.getInstance().getUser()));
        assertTrue(result.containsKey(CommonString.getInstance().getPassword()));
        
        assertEquals(result.get(CommonString.getInstance().getHost()), emailHost);
        assertEquals(result.get(CommonString.getInstance().getPort()), emailPort);
        assertEquals(result.get(CommonString.getInstance().getFrom()), emailFrom);
        assertEquals(result.get(CommonString.getInstance().getSSL()), Boolean.FALSE.toString());
        assertEquals(result.get(CommonString.getInstance().getStrtTTLS()), Boolean.TRUE.toString());
        assertEquals(result.get(CommonString.getInstance().getAuth()), Boolean.TRUE.toString());
        assertEquals(result.get(CommonString.getInstance().getUser()), emailUsername);
        assertEquals(result.get(CommonString.getInstance().getPassword()), emailPassword); 
    }

    /**
     * Test of extractUserIdFromString method, of class KeycloakHelper.
     */
    @Test
    public void testExtractUserIdFromString() {
        System.out.println("extractUserIdFromString");
        
        String string = "http://sso:8080/auth/admin/realms/dina/users/8a86adb2-c214-462c-bc17-3deef9a73304";
  
        instance = new KeycloakHelper();
 
        String result = instance.extractUserIdFromString(string);
        assertEquals("8a86adb2-c214-462c-bc17-3deef9a73304", result); 
    }
    
}
