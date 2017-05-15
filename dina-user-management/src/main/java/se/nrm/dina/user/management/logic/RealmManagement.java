/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.json.JsonObject;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder; 
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
public class RealmManagement implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());  
    
    private Keycloak keycloakClient;
    
    @Inject
    public JsonConverter json;
    
    public RealmManagement() {
        
    }
    
    public JsonObject getRealmByRealmName(String realmName) {
        buildRealm();
        
        RealmResource realmResource = keycloakClient.realm(realmName); 
        List<RoleRepresentation> roleRepresentations = realmResource.roles().list();
        List<String> realmRoles = roleRepresentations.stream()
                                                     .filter(realmRolesPredicate())
                                                     .map(r -> r.getName())
                                                     .collect(Collectors.toList());
                 
        ClientsResource clientsResource = realmResource.clients();
        List<ClientRepresentation> clientRepresentations = clientsResource.findAll();
        
        ClientRepresentation dinaRestClientRepresentation = getClientRepresentation(clientRepresentations, 
                                                                                    CommonString.getInstance().getDinaRestClientId());
        List<String> dinaRestRoles = Arrays.asList(dinaRestClientRepresentation.getDefaultRoles());
        
        ClientRepresentation userManagementClientRepresentation = getClientRepresentation(clientRepresentations, 
                                                                                          CommonString.getInstance().getUserManagementClientId());
        List<String> userManagementRoles = Arrays.asList(userManagementClientRepresentation.getDefaultRoles());
        Map<String, List<String>> roleMap = new HashMap<>();
        roleMap.put(CommonString.getInstance().getDinaRestClientId(), dinaRestRoles);
        roleMap.put(CommonString.getInstance().getUserManagementClientId(), userManagementRoles);
        
        
        return null;
    }
    
    private ClientRepresentation getClientRepresentation(List<ClientRepresentation> clientRepresentations, String clientId) {
        return clientRepresentations.stream() 
                                    .filter(clientRepresentationPredicate(clientId)) 
                                    .findFirst() 
                                    .get();
    }
    
    private static Predicate<RoleRepresentation> realmRolesPredicate() {
        return r -> !r.getName().equals("uma_authorization") && !r.getName().equals("offline_access");
    }
    
    private static Predicate<ClientRepresentation> clientRepresentationPredicate(String clientId) {
        return c -> c.getClientId().equals(clientId);
    }
    
    public void createRealm(String realmName) {
        RealmRepresentation rr = new RealmRepresentation();
        rr.setDuplicateEmailsAllowed(false);
        rr.setRealm("myrealm");
        keycloakClient.realms().create(rr);
    }

    private void buildRealm() {   
        keycloakClient = KeycloakBuilder.builder()
                                        .serverUrl(CommonString.getInstance().getKeyCloakLUrl()) //
                                        .realm(CommonString.getInstance().getMastRealm())//
                                        .username(CommonString.getInstance().getMasterAdminUsrname()) //
                                        .password(CommonString.getInstance().getMasterAdminPassword()) //
                                        .clientId(CommonString.getInstance().getAdminClientId())
                                        .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
                                        .build();
    }
}
