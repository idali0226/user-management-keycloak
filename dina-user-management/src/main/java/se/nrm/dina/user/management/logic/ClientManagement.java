/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;   
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject; 
import javax.json.JsonObject;
import org.keycloak.admin.client.Keycloak; 
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.logic.helpers.KeycloakClientHelper;  
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */ 
public class ClientManagement implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());  
    private Keycloak keycloakClient;
    
    @Inject
    public JsonConverter json;
    
    public ClientManagement() {
        
    }
    
    @PostConstruct
    public void init() {
        logger.info("init"); 
        
        keycloakClient = KeycloakClientHelper.getInstance().buildKeycloakClient(); 
    }
    
    public JsonObject getAllTheClients() {
 
        List<ClientRepresentation> clientsRepresetation = keycloakClient.realm(CommonString.getInstance().getDinaRealm()).clients().findAll();
        
        Map<ClientRepresentation, List<RoleRepresentation>> map = new HashMap();
        clientsRepresetation.stream() 
                .filter(c -> !c.getName().contains("client")) 
                .forEach(c -> {
                    RolesResource rolesResource = keycloakClient.realm(CommonString.getInstance()
                                                                .getDinaRealm())
                                                                .clients().get(c.getId()).roles(); 
                    map.put(c, rolesResource.list());
                });
          
        return json.converterClients(map); 
    }
    
    
//    public ClientRepresentation createEndpointClient() {
//        ClientRepresentation clientRepresentation = new ClientRepresentation();
//        clientRepresentation.setClientId(CommonString.getInstance().getDinaRestClientId());
//        clientRepresentation.setName(CommonString.getInstance().getDinaRestClientName());
//        clientRepresentation.setDescription("dina rest endpoint to retrieve token");
//        clientRepresentation.setEnabled(Boolean.TRUE);
//        clientRepresentation.setProtocol(CommonString.getInstance().getOpenIdConnectionProtocol());
//        clientRepresentation.setDirectAccessGrantsEnabled(Boolean.TRUE);
//
//        List<ProtocolMapperRepresentation> protocolMappers = new ArrayList();
//        setupProtocolMapper(protocolMappers,
//                CommonString.getInstance().getRealmRoleClaimName(),
//                CommonString.getInstance().getProtocolMapperConsentTextRealmRole(),
//                CommonString.getInstance().getOpenIdConnectionProtocol(),
//                CommonString.getInstance().getProtocolMapperRealmRoleMapper(),
//                Util.getInstance().buildProtocolMap(
//                        CommonString.getInstance().getRealmRoleClaimName(),
//                        CommonString.getInstance().getRealmRoleClaimName()));
//
//        setupProtocolMapper(protocolMappers,
//                CommonString.getInstance().getEmail(),
//                CommonString.getInstance().getProtocolMapperConsentTextEmail(),
//                CommonString.getInstance().getOpenIdConnectionProtocol(),
//                CommonString.getInstance().getProtocolMapperPropertyMapper(),
//                Util.getInstance().buildProtocolMap(CommonString.getInstance().getEmail(),
//                        CommonString.getInstance().getEmail()));
//
//        setupProtocolMapper(protocolMappers,
//                CommonString.getInstance().getUsername(),
//                CommonString.getInstance().getProtocolMapperConsentTextUsername(),
//                CommonString.getInstance().getOpenIdConnectionProtocol(),
//                CommonString.getInstance().getProtocolMapperPropertyMapper(),
//                Util.getInstance().buildProtocolMap(CommonString.getInstance().getUsername(),
//                        CommonString.getInstance().getPreferredUsername()));
//
//        setupProtocolMapper(protocolMappers,
//                CommonString.getInstance().getProtocolMapperNameFalimyName(),
//                CommonString.getInstance().getProtocolMapperConsentTextFamilyName(),
//                CommonString.getInstance().getOpenIdConnectionProtocol(),
//                CommonString.getInstance().getProtocolMapperPropertyMapper(),
//                Util.getInstance().buildProtocolMap(CommonString.getInstance().getTokenLastName(),
//                        CommonString.getInstance().getTokenFamilyName()));
//
//        setupProtocolMapper(protocolMappers,
//                CommonString.getInstance().getProtocolMapperNameGivenName(),
//                CommonString.getInstance().getProtocolMapperConsentTextGivenName(),
//                CommonString.getInstance().getOpenIdConnectionProtocol(),
//                CommonString.getInstance().getProtocolMapperPropertyMapper(),
//                Util.getInstance().buildProtocolMap(CommonString.getInstance().getTokenFirstName(),
//                        CommonString.getInstance().getTokenGivenName()));
//        
//        clientRepresentation.setProtocolMappers(protocolMappers);
//
//        List<String> redirectURIs = new ArrayList<>();
//        redirectURIs.add(CommonString.getInstance().getRedirectFrontEndURL());
//        clientRepresentation.setRedirectUris(redirectURIs);
//
//        return clientRepresentation;
////        realmResource.clients().create(clientRepresentation);
//    }
//    
//    private void setupProtocolMapper(List<ProtocolMapperRepresentation> protocolMappers,   
//                                     String name, String consentText, String protocol, 
//                                     String protocolMapper, Map<String, String> configMap) { 
//        
//        ProtocolMapperRepresentation protocolMapperRepresentation = new ProtocolMapperRepresentation();
//        protocolMapperRepresentation.setName(name);
//        protocolMapperRepresentation.setConsentText(consentText);
//        protocolMapperRepresentation.setProtocol(protocol);
//        protocolMapperRepresentation.setConsentRequired(true); 
//        protocolMapperRepresentation.setProtocolMapper(protocolMapper); 
//        protocolMapperRepresentation.setConfig(configMap); 
//        protocolMappers.add(protocolMapperRepresentation);
//    }
// 
//    

         
//    private void buildRealm() {   
//        keycloakClient = KeycloakBuilder.builder()
//                                        .serverUrl(CommonString.getInstance().getKeyCloakLUrl()) //
//                                        .realm(CommonString.getInstance().getMastRealm())//
//                                        .username(CommonString.getInstance().getMasterAdminUsrname()) //
//                                        .password(CommonString.getInstance().getMasterAdminPassword()) //
//                                        .clientId(CommonString.getInstance().getAdminClientId())
//                                        .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
//                                        .build(); 
//    }
    
    @PreDestroy
    public void preDestroy() {
        logger.info("preDestroy"); 
        if (keycloakClient != null) {
            keycloakClient.close();
            logger.info("keycloakClient is closed");
        }
    }
}
