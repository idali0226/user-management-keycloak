/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;   
import java.util.ArrayList;
import java.util.List; 
import javax.inject.Inject; 
import javax.json.JsonObject; 
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;  
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.utils.CommonString;
import se.nrm.dina.user.management.utils.TempPasswordGenerator;

/**
 *
 * @author idali
 */ 
public class UserManagement implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());  
    private Keycloak keycloakClient;
    
    @Inject
    public JsonConverter json;
    
    public UserManagement() {  
    }

    public void createUser(String jsonString) {
        logger.info("createuser");
        JsonObject jsonObject = json.readInJson(jsonString);

        String firstName = jsonObject.getString(CommonString.getInstance().getFirstName());
        String lastName = jsonObject.getString(CommonString.getInstance().getLastName());
        String email = jsonObject.getString(CommonString.getInstance().getEmail());
        String userName = jsonObject.getString(CommonString.getInstance().getUsername()); 

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        user.setEmail(email);
        user.setEnabled(true);
        user.setEmailVerified(true);

//        user.setAttributes(attributes);
        Response response = getUsersResource().create(user);
        String locationHeader = response.getHeaderString(CommonString.getInstance().getLocation());
        response.close();

        if (locationHeader != null) {
            UserResource userResource = getUsersResource().get(locationHeader
                                                    .replaceAll(CommonString.getInstance().getREGEX(), 
                                                                CommonString.getInstance().getREGEX1()));
            resetPassword(userResource);
            setReamlRole(userResource);
            setClientRole(CommonString.getInstance().getDinaRestClientId(), userResource);
            setClientRole(CommonString.getInstance().getUserManagementClientId(), userResource);
        } 
    }
    
    private void setClientRole(String clientId, UserResource userResource) {

        List<ClientRepresentation> crs = getClientsResource().findAll();
        ClientRepresentation cr = crs.stream()
                .filter(c -> c.getClientId().equals(clientId))
                .findFirst().get();
        
        String cId = cr.getId();

        List<RoleRepresentation> clrs = getClientsResource().get(cId).roles().list();
        clrs.stream()
                .forEach(rr -> {
                    if (rr.getName().equals(CommonString.getInstance().getAdminRole())) {
                        userResource.roles().clientLevel(cId).add(clrs);
                    }
                }); 
    }

    
    
    private void setReamlRole(UserResource userResource) {
        List<RoleRepresentation> dinaRealmRoles = getDinaRealmResource().roles().list();

        List<RoleRepresentation> newRole = new ArrayList<>();
        dinaRealmRoles.stream()
                        .forEach(drr -> { 
                            if(drr.getName().equals(CommonString.getInstance().getAdminRole())) {
                                newRole.add(drr);
                            }
                            userResource.roles().realmLevel().add(newRole);
                        }); 
    }
    
    private ClientsResource getClientsResource() {
        return getDinaRealmResource().clients();
    }
    
    
    private UsersResource getUsersResource() {
        return getDinaRealmResource().users();
    }
    
    private RealmResource getDinaRealmResource() {
        return keycloakClient.realm(CommonString.getInstance().getDinaRealm());
    }

    private void resetPassword(UserResource userResource) {

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(TempPasswordGenerator.generateRandomPassword());
        cred.setTemporary(true);
     
        userResource.resetPassword(cred);
    }


    
    public JsonObject getUsers(String realm) {
        logger.info("getUsers"); 
        buildRealm();
        List<UserRepresentation> list = keycloakClient.realm(realm).users().search(null, 0, 20);
        keycloakClient.close();
        return json.converterUsers(list); 
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
