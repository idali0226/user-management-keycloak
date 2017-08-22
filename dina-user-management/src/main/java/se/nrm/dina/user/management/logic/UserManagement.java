/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors; 
import javax.inject.Inject;
import javax.json.JsonObject;  
import javax.ws.rs.core.Response; 
import lombok.extern.slf4j.Slf4j; 
import org.keycloak.admin.client.Keycloak;  
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;  
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.keycloak.KeycloakClient; 
import se.nrm.dina.user.management.utils.AccountStatus;
import se.nrm.dina.user.management.utils.CommonString; 

/**
 *
 * @author idali
 */
@Slf4j
public class UserManagement implements Serializable {
  
    @Inject
    public JsonConverter json;
     
    @Inject
    @KeycloakClient
    private Keycloak keycloakClient;
    
    @Inject
    @KeycloakClient
    private String dinaRealm;

    public UserManagement() {
    }
 
    public JsonObject createUser(String jsonString, boolean createdByAdmin) {
        log.info("createuser : {}", createdByAdmin);

        JsonObject dataJson = json.readInJson(jsonString).getJsonObject(CommonString.getInstance().getData());
        JsonObject attributesJson = dataJson.getJsonObject(CommonString.getInstance().getAttributes());

        String firstName = attributesJson.getString(CommonString.getInstance().getFirstName());
        String lastName = attributesJson.getString(CommonString.getInstance().getLastName());
        String email = attributesJson.getString(CommonString.getInstance().getEmail());
        String purpose = attributesJson.getString(CommonString.getInstance().getPurpose());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setEnabled(true);                              // Note: Must be enabled.  Otherwies can not send email
        user.setEmailVerified(createdByAdmin);

        String status = createdByAdmin ? AccountStatus.Enabled.name() : AccountStatus.Pending.name();
        user.singleAttribute(CommonString.getInstance().getStatus(), status);
        user.singleAttribute(CommonString.getInstance().getPurpose(), purpose);

        Response response = getDinaRealmResource().users().create(user);
        String locationHeader = response.getHeaderString(CommonString.getInstance().getLocation());
        response.close();

        UserRepresentation userRespresentation = null;
        if (locationHeader != null) {
            UserResource userResource = getUsersResource()
                    .get(locationHeader
                            .replaceAll(CommonString.getInstance().getREGEX(),
                                    CommonString.getInstance().getREGEX1()));
            if (createdByAdmin) {
                setRealmRole(userResource, CommonString.getInstance().getUserRole());
                setClientRole(userResource, CommonString.getInstance().getDinaRestClientId(), CommonString.getInstance().getUserRole());
                setClientRole(userResource, CommonString.getInstance().getUserManagementClientId(), CommonString.getInstance().getUserRole());
//                 userResource.resetPasswordEmail();
            } else {
                removeRealmRoles(userResource);
//                 userResource.sendVerifyEmail();
            }
            userRespresentation = userResource.toRepresentation();
        }
        return json.converterUser(userRespresentation, null, null);
    }

    public JsonObject sendEmail(String id, boolean isPendingUser) {
        log.info("sendEmail : {}", id);

        UserResource userResource = getUsersResource().get(id); 
        if(isPendingUser) {
            userResource.sendVerifyEmail();
        } else {
            userResource.resetPasswordEmail();
        } 
        return json.converterUser(userResource.toRepresentation(), null, null);
    }

    public JsonObject recoverPassword(String email) {
        log.info("recoverPassword : {}", email);

        List<UserRepresentation> userRepresentations = getUsersRepresentation(email);
        if (userRepresentations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Eamil: ");
            sb.append(email);
            sb.append(" not exist.");
            return json.buildErrorMessages(sb.toString(), null);
        } else { 
            UserRepresentation userRepresentation = userRepresentations.get(0);
            if (getAccountStatus(userRepresentation).equals(AccountStatus.Enabled.name())) { 
                UserResource userResource = getUsersResource().get(userRepresentation.getId());
                userResource.resetPasswordEmail();
                return json.successJson("Email sent successfully");
            }
            return json.successJson("User email address is not verified or user is not enabled. Update password email do not send.");
        }
    }
    
    private String getAccountStatus(UserRepresentation userRepresentation) {
        Map<String, List<String>> status = userRepresentation.getAttributes();
        return status.get(CommonString.getInstance().getStatus()).get(0);
    }

    /**
     * Disable a user from keycloak server
     *
     * @param id
     * @return user in JsonObject format
     */
    public JsonObject disableUser(String id) {
        log.info("disableLUser");

        UserResource userResource = getUsersResource().get(id);
        removeCientRoles(userResource, CommonString.getInstance().getDinaRestClientId());
        removeCientRoles(userResource, CommonString.getInstance().getUserManagementClientId());
        setRealmRole(userResource, CommonString.getInstance().getDisabledUserRole());

        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEnabled(Boolean.FALSE);
        userRepresentation.singleAttribute(CommonString.getInstance().getStatus(), AccountStatus.Disabled.name());

        userResource.update(userRepresentation);

        return getUserById(id);
    }

    /**
     * Remove user from keycloak server
     *
     * @param id
     * @return String
     */
    public JsonObject rejectUser(String id) {
        log.info("rejectUser");
        
        UserResource userResource = getUsersResource().get(id);
        removeCientRoles(userResource, CommonString.getInstance().getDinaRestClientId());
        removeCientRoles(userResource, CommonString.getInstance().getUserManagementClientId());
        setRealmRole(userResource, CommonString.getInstance().getDisabledUserRole());

        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEnabled(Boolean.FALSE);
        userRepresentation.singleAttribute(CommonString.getInstance().getStatus(), AccountStatus.Rejected.name());

        userResource.update(userRepresentation);
  
        return getUsers();
    }
    
    public JsonObject deleteUser(String id) {
        log.info("deleteUser");
        
        getUsersResource().delete(id);
        return getUsers();
    }

    /**
     * Enable user by given user id in Keycloak server
     *
     * @param id (value = user id)
     * @return user in JsonFormat
     */
    public JsonObject enableUser(String id) {
        log.info("enableUser");

        UserResource userResource = getUsersResource().get(id);
        UserRepresentation userRepresentation = userResource.toRepresentation();

        if (!userRepresentation.isEmailVerified()) {
            return json.buildErrorMessages("User can not be enabled", null);
        } else {
            boolean resetPassword = false;
            
            Map<String, List<String>> attributes = userRepresentation.getAttributes();
            String status = attributes.get(CommonString.getInstance().getStatus()).get(0);

            if (status.equals(AccountStatus.Pending.name())) {
                resetPassword = true;
            }

            userRepresentation.singleAttribute(CommonString.getInstance().getStatus(), AccountStatus.Enabled.name()); 
            setRealmRole(userResource, CommonString.getInstance().getUserRole());
            setClientRole(userResource, CommonString.getInstance().getDinaRestClientId(), CommonString.getInstance().getUserRole());
            setClientRole(userResource, CommonString.getInstance().getUserManagementClientId(), CommonString.getInstance().getUserRole());

            userRepresentation.setEnabled(Boolean.TRUE);
            userResource.update(userRepresentation);

            if (resetPassword) {
                userResource.resetPasswordEmail();
            } 
            return getUserById(id);
        }
    }

    public JsonObject updateUser(String jsonString) {
        log.info("updateUser");
        JsonObject dataJson = json.readInJson(jsonString).getJsonObject(CommonString.getInstance().getData());
        JsonObject attributesJson = dataJson.getJsonObject(CommonString.getInstance().getAttributes());

        String firstName = attributesJson.getString(CommonString.getInstance().getFirstName());
        String lastName = attributesJson.getString(CommonString.getInstance().getLastName()); 
        String purpose = attributesJson.getString(CommonString.getInstance().getPurpose());
         
        String id = dataJson.getString(CommonString.getInstance().getId());

        UserResource userResource = getUsersResource().get(id);
        
        String jsonPassword = attributesJson.get(CommonString.getInstance().getPassword()).toString();
        if(!jsonPassword.equals("null") && !jsonPassword.isEmpty()) {
            String password = attributesJson.getString(CommonString.getInstance().getPassword());
            resetCredential(userResource, password);   
        }
        
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName); 
 
        userRepresentation.singleAttribute(CommonString.getInstance().getPurpose(), purpose);
        userResource.update(userRepresentation); 

        return json.converterUser(userRepresentation, null, null);
    }

    /**
     * Logout user by given id from keycloak server
     *
     * @param id (value = userId)
     * @return String in JsonFormat
     */
    public JsonObject logout(String id) {
        log.info("logout : {}", id);

        getDinaRealmResource().users().get(id).logout();
        return json.successJson("User logout success");
    }

    /**
     * Returns an user has given id
     *
     * @param id
     * @return Single user in JsonObject format
     */
    public JsonObject getUserById(String id) {
        log.info("getUserById");
         
         
        UserResource userResource = getUserResourceById(id); 
        
        List<RoleRepresentation> effictiveRealmRepresentation = userResource.roles().realmLevel().listEffective();
   
        List<RoleRepresentation> userManagementClientRoles = userResource.roles()
                                                                .clientLevel(getClientIdByClientName(CommonString.getInstance()
                                                                .getUserManagementClientId()))
                                                                .listEffective();
        List<RoleRepresentation> restServiceClientRoles = userResource.roles()
                                                                .clientLevel(getClientIdByClientName( CommonString.getInstance()
                                                                        .getDinaRestClientId()))
                                                                .listEffective();
 
        Map<String, List<RoleRepresentation>> clientRoles = new HashMap<>();
        clientRoles.put(CommonString.getInstance().getUserManagementClientId(), userManagementClientRoles);
        clientRoles.put(CommonString.getInstance().getDinaRestClientId(), restServiceClientRoles);
        return json.converterUser(userResource.toRepresentation(), effictiveRealmRepresentation, clientRoles);
    }

    /**
     * Returns an user has given username
     *
     * @param userName
     * @return Single user in JsonObject format
     */
    public JsonObject getUserByUserName(String userName) {
     
        return json.converterUsers(getUsersResource().search(userName));
    }
    
    public JsonObject getUserByAccountStatus(String status) {
        log.info("getUserByAccountStatus : {}", status);
        List<UserRepresentation> list = getUsersRepresentation(null);
        
        List<UserRepresentation> matchList = list.stream()
                                                  .filter(u -> u.getAttributes().get("status").get(0).equals(status))
                                                  .collect(Collectors.toList());
        return json.converterUsers(matchList);
    }

    /**
     * Return a list of all the users registered in Keycloak
     *
     * @return a list of all the users in JsonObject format
     */
    public JsonObject getUsers() {
        log.info("getUsers");
        return json.converterUsers(getUsersRepresentation(null));
    }

    /**
     * Returns a list of logged in users
     *
     * @return the list of logged in users in JsonObject format
     */
    public JsonObject getLoggedInUser() {
        log.info("getLoggedInUser");

        UsersResource usersResource = getUsersResource();
        int count = usersResource.count();
        List<UserRepresentation> userRepresentations = usersResource.search(null, 0, count);
        List<UserRepresentation> loggedInUsers = userRepresentations.stream()
                .parallel()
                .filter(isLoggedIn(usersResource))
                .collect(Collectors.toList());

        return json.converterUsers(loggedInUsers);
    }
    
    private void resetCredential(UserResource userResource, String password) { 
        log.info("resetCredential : {}", password);
          
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        userResource.resetPassword(credential); 
    }
 
    private List<UserRepresentation> getUsersRepresentation(String email) {
        UsersResource usersResource = getUsersResource(); 
        return usersResource.search(email, 0, usersResource.count());
    }

    private UserRepresentation getUserRepresentationById(String id) {
        UserResource userResource = getUserResourceById(id);
        
        return userResource != null ? userResource.toRepresentation() : null;
    }

    private UserResource getUserResourceById(String id) {
        return getUsersResource().get(id);
    }
    
    private UsersResource getUsersResource() {
        return getDinaRealmResource().users();
    }

    private void setClientRole(UserResource userResource, String clientId, String roleName) {

        List<RoleRepresentation> newRole = getClientRolesByClientId(clientId).stream()
                .filter(clientRole -> clientRole.getName().equals(roleName))
                .collect(Collectors.toList());
        userResource.roles().clientLevel(getClientIdByClientName(clientId)).add(newRole);
    }

    private void removeCientRoles(UserResource userResource, String clientId) {
        List<RoleRepresentation> clrs = getClientRolesByClientId(clientId);
        userResource.roles().clientLevel(getClientIdByClientName(clientId)).remove(clrs);
    }

    private List<RoleRepresentation> getClientRolesByClientId(String clientId) {
        String cId = getClientIdByClientName(clientId);
        return cId == null ? new ArrayList() : getClientsResource().get(cId).roles().list();
    }

    private String getClientIdByClientName(String clientName) {
        ClientRepresentation clientRepresentation = getClientRepresentationByClientId(clientName);
        return clientRepresentation == null ? null : clientRepresentation.getId();
    }

    private ClientRepresentation getClientRepresentationByClientId(String clientId) {
        List<ClientRepresentation> clientRepresentation = getClientsResource().findByClientId(clientId);
        return clientRepresentation.isEmpty() ? null : clientRepresentation.get(0);
    }

    private ClientsResource getClientsResource() {
        return getDinaRealmResource().clients();
    }

    private void setRealmRole(UserResource userResource, String role) {

        removeRealmRoles(userResource);
        List<RoleRepresentation> dinaRealmRoles = getDinaRealmResource().roles().list();
        List<RoleRepresentation> newRole = dinaRealmRoles.stream()
                .filter(isNotDefaultRealmRole(CommonString.getInstance().getOfflineAccessRole()))
                .filter(isNotDefaultRealmRole(CommonString.getInstance().getUmaAuthorizationRole()))
                .filter(isRealmRole(role))
                .collect(Collectors.toList());
        userResource.roles().realmLevel().add(newRole);
    }

    private void removeRealmRoles(UserResource userResource) {
        List<RoleRepresentation> realmRoleRepresentations = userResource.roles().realmLevel().listAll();
        userResource.roles().realmLevel().remove(realmRoleRepresentations);
    }

    private RealmResource getDinaRealmResource() {
        return keycloakClient.realm(dinaRealm);
    }

    private static Predicate<RoleRepresentation> isRealmRole(String roleName) {
        return role -> role.getName().equals(roleName);
    }

    private static Predicate<RoleRepresentation> isNotDefaultRealmRole(String realmRoleName) {
        return role -> !role.getName().equals(realmRoleName);
    }

    private static Predicate<UserRepresentation> isLoggedIn(UsersResource usersResource) {
        return u -> !usersResource.get(u.getId()).getUserSessions().isEmpty();
    } 
} 