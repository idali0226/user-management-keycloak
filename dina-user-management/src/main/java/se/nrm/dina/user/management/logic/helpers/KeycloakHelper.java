/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.helpers;

import java.util.HashMap;
import java.util.Map;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 *
 * Help class to setup data which support logic classes
 */
public class KeycloakHelper {

    /**
     * Build UserRepresentation by given parameters
     *
     * @param username
     * @param firstname
     * @param lastname
     * @param purpose
     * @param emailVerfied
     * @param accountStatus
     *
     * @return UserRepresentation
     */
    public UserRepresentation buildUserRepresentation(String username, String firstname, String lastname,
                                                      String purpose, boolean emailVerfied, Enum accountStatus) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(username);
        userRepresentation.setEmail(username);
        userRepresentation.setFirstName(firstname);
        userRepresentation.setLastName(lastname);
        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setEmailVerified(emailVerfied);

        userRepresentation.singleAttribute(CommonString.getInstance().getStatus(), accountStatus.name());
        userRepresentation.singleAttribute(CommonString.getInstance().getPurpose(), purpose);
        return userRepresentation;
    }

    /**
     * Build ClientRepresentation by given parameters
     *
     * @param clientId
     * @param clientName
     * @param clientDescription
     * @param directAccessGrantsEnable
     *
     * @return ClientRepresentation
     */
    public ClientRepresentation buildClientRepresentation(String clientId, String clientName,
                                                          String clientDescription, boolean directAccessGrantsEnable) {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(clientId);
        clientRepresentation.setName(clientName);
        clientRepresentation.setDescription(clientDescription);
        clientRepresentation.setEnabled(Boolean.TRUE);
        clientRepresentation.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
        clientRepresentation.setDirectAccessGrantsEnabled(directAccessGrantsEnable);
        if (!directAccessGrantsEnable) {
            clientRepresentation.setBearerOnly(Boolean.TRUE);
        }
        return clientRepresentation;
    }

    /**
     * Build ProtocolMapperRepresentation by given parameters
     *
     * @param name
     * @param consentText
     * @param protocol
     * @param protocolMapper
     * @param configMap
     *
     * @return ProtocolMapperRepresentation
     */
    public ProtocolMapperRepresentation buildProtocolMapper(String name, String consentText, String protocol,
                                                            String protocolMapper, Map<String, String> configMap) {
        ProtocolMapperRepresentation protocolMapperRepresentation = new ProtocolMapperRepresentation();
        protocolMapperRepresentation.setName(name);
        protocolMapperRepresentation.setConsentText(consentText);
        protocolMapperRepresentation.setProtocol(protocol);
        protocolMapperRepresentation.setConsentRequired(true);
        protocolMapperRepresentation.setProtocolMapper(protocolMapper);
        protocolMapperRepresentation.setConfig(configMap);
        return protocolMapperRepresentation;
    }

    /**
     * Build Map<String, String>
     *
     * @param attribute
     * @param claimName
     *
     * @return Map<String, String>
     */
    public Map<String, String> buildProtocolMap(String attribute, String claimName) {
        Map<String, String> protocolMap = new HashMap<>();
        protocolMap.put(CommonString.getInstance().getMultiValued(), Boolean.TRUE.toString());
        protocolMap.put(CommonString.getInstance().getUserInfoTokenClaim(), Boolean.TRUE.toString());
        protocolMap.put(CommonString.getInstance().getUserAttribute(), attribute);
        protocolMap.put(CommonString.getInstance().getIdTokenClaim(), Boolean.TRUE.toString());
        protocolMap.put(CommonString.getInstance().getAccessTokenClaim(), Boolean.TRUE.toString());
        protocolMap.put(CommonString.getInstance().getClientName(), claimName);
        protocolMap.put(CommonString.getInstance().getJsonTypeLabel(), CommonString.getInstance().getStringDataFormat());

        return protocolMap;
    }

    /**
     * Build CredentialRepresentation
     * 
     * @param password
     * @param isTemporary
     * 
     * @return CredentialRepresentation
     */
    public CredentialRepresentation buildCredentialRepresentation(String password, boolean isTemporary) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setTemporary(isTemporary);
        return credentialRepresentation;
    }

    /**
     * Build a basic roles map which contains of admin role and user role
     * 
     * @return Map<String, String>
     */
    public Map<String, String> setupBasicRoleMap() {
        Map<String, String> basicRoleMap = new HashMap();
        basicRoleMap.put(CommonString.getInstance().getAdminRole(), CommonString.getInstance().getAdminRoleDescription());
        basicRoleMap.put(CommonString.getInstance().getUserRole(), CommonString.getInstance().getUserRoleDescription());
        return basicRoleMap;
    }

    /**
     * Build a realm role map which contains basic roles admin and user, and additional roles disable user and super admin
     * 
     * @return Map<String, String> 
     */
    public Map<String, String> setupRealmRoleMap() {
        Map<String, String> realmRoleMap = setupBasicRoleMap();
        realmRoleMap.put(CommonString.getInstance().getSuperAdminRole(), CommonString.getInstance().getSuperAdminRole());
        realmRoleMap.put(CommonString.getInstance().getDisabledUserRole(), CommonString.getInstance().getDisabledRoleDescription());
        return realmRoleMap;
    }

    /**
     * Build a map configurations for realm server
     * 
     * @param emailHost
     * @param emailPort
     * @param emailFrom
     * @param emailUsername
     * @param emailPassword
     * 
     * @return Map<String, String>
     */
    public Map<String, String> buildSMTPMailMap(String emailHost, String emailPort, String emailFrom,
                                                String emailUsername, String emailPassword) {
        Map<String, String> smtpServerMap = new HashMap<>();
        smtpServerMap.put(CommonString.getInstance().getHost(), emailHost);
        smtpServerMap.put(CommonString.getInstance().getPort(), emailPort);
        smtpServerMap.put(CommonString.getInstance().getFrom(), emailFrom);
        smtpServerMap.put(CommonString.getInstance().getSSL(), Boolean.FALSE.toString());
        smtpServerMap.put(CommonString.getInstance().getStrtTTLS(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getAuth(), Boolean.TRUE.toString());
        smtpServerMap.put(CommonString.getInstance().getUser(), emailUsername);
        smtpServerMap.put(CommonString.getInstance().getPassword(), emailPassword);
        return smtpServerMap;
    }

    /**
     * This method use regular expression to extract user id from the response String from create user
     * 
     * @param string
     * @return String
     */
    public String extractUserIdFromString(String string) { 
        return string.replaceAll(CommonString.getInstance().getREGEX(), CommonString.getInstance().getREGEX1());
    }

}
