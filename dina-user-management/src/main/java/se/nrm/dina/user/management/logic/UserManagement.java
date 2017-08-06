/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
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
import se.nrm.dina.user.management.logic.email.MailHandler;
import se.nrm.dina.user.management.utils.AccountStatus;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
public class UserManagement implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Keycloak keycloakClient;

    @Inject
    public MailHandler mail;

    @Inject
    public JsonConverter json;

    public UserManagement() {
    }

    @PostConstruct
    public void init() {
        logger.info("init");
        buildRealm();
    }

    public JsonObject createUser(String jsonString, boolean createdByAdmin) {
        logger.info("createuser : {}", createdByAdmin);

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

        Response response = keycloakClient.realm(CommonString.getInstance().getDinaRealm()).users().create(user);
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
//                 userResource.sendVerifyEmail();
            }
            userRespresentation = userResource.toRepresentation();
        }
        return json.converterUser(userRespresentation);
    }

    public JsonObject sendEmail(String id, boolean isPendingUser) {
        logger.info("sendEmail : {}", id);

        UserResource userResource = getUsersResource().get(id); 
        if(isPendingUser) {
            userResource.sendVerifyEmail();
        } else {
            userResource.resetPasswordEmail();
        } 
        return json.converterUser(userResource.toRepresentation());
    }

    public JsonObject recoverPassword(String email) {
        logger.info("recoverPassword : {}", email);

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
        logger.info("disableLUser");

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
        logger.info("rejectUser");
        
        UserResource userResource = getUsersResource().get(id);
        removeCientRoles(userResource, CommonString.getInstance().getDinaRestClientId());
        removeCientRoles(userResource, CommonString.getInstance().getUserManagementClientId());
        setRealmRole(userResource, CommonString.getInstance().getDisabledUserRole());

        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEnabled(Boolean.FALSE);
        userRepresentation.singleAttribute(CommonString.getInstance().getStatus(), AccountStatus.Rejected.name());

        userResource.update(userRepresentation);
 

//        UserResource userResource = getUsersResource().get(id);
//        userResource.remove();
    
        return getUsers();
    }
    
    public JsonObject deleteUser(String id) {
        logger.info("deleteUser");
        
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
        logger.info("enableUser");

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
        logger.info("updateUser");
        JsonObject dataJson = json.readInJson(jsonString).getJsonObject(CommonString.getInstance().getData());
        JsonObject attributesJson = dataJson.getJsonObject(CommonString.getInstance().getAttributes());

        String firstName = attributesJson.getString(CommonString.getInstance().getFirstName());
        String lastName = attributesJson.getString(CommonString.getInstance().getLastName());
    //    String email = attributesJson.getString(CommonString.getInstance().getEmail());
        String purpose = attributesJson.getString(CommonString.getInstance().getPurpose());
         
        String id = dataJson.getString(CommonString.getInstance().getId());

        UserResource userResource = getUsersResource().get(id);
        
        if(!attributesJson.get(CommonString.getInstance().getPassword()).toString().equals("null")) {
            String password = attributesJson.getString(CommonString.getInstance().getPassword());
            resetCredential(userResource, password);   
        }
        
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);
  //      userRepresentation.setEmail(email);
  //      userRepresentation.setUsername(email);
 
        userRepresentation.singleAttribute(CommonString.getInstance().getPurpose(), purpose);
        userResource.update(userRepresentation);
        
//        String password = attributesJson.get(CommonString.getInstance().getPassword()).toString();  
//        if(password != null && !password.isEmpty() && !password.equals("null")) {  
//            resetCredential(userResource, password); 
//        } 

        return json.converterUser(userRepresentation);
    }

    /**
     * Logout user by given id from keycloak server
     *
     * @param id (value = userId)
     * @return String in JsonFormat
     */
    public JsonObject logout(String id) {
        logger.info("logout : {}", id);

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
        logger.info("getUserById");
         
        return json.converterUser(getUserRepresentationById(id));
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
        logger.info("getUserByAccountStatus : {}", status);
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
        logger.info("getUsers");
        return json.converterUsers(getUsersRepresentation(null));
    }

    /**
     * Returns a list of logged in users
     *
     * @return the list of logged in users in JsonObject format
     */
    public JsonObject getLoggedInUser() {
        logger.info("getLoggedInUser");

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
        logger.info("resetCredential : {}", password);
        
        logger.info("user resource : {}", userResource.toRepresentation().getEmail() );
         
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
        UserResource userResource = getUsersResource().get(id);
        
        return userResource != null ? userResource.toRepresentation() : null;
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
        return keycloakClient.realm(CommonString.getInstance().getDinaRealm());
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

    private void buildRealm() {

        String keycloakAuthURL = System.getenv(CommonString.getInstance().getKeycloakURI());
        logger.info("keycloakAuthURL : {}", keycloakAuthURL);

        if (keycloakAuthURL.isEmpty()) {
            keycloakAuthURL = "http://localhost:8080/auth";
        }
        keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthURL) //
                .realm(CommonString.getInstance().getMastRealm())//
                .username(CommonString.getInstance().getMasterAdminUsrname()) //
                .password(CommonString.getInstance().getMasterAdminPassword()) //
                .clientId(CommonString.getInstance().getAdminClientId())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
                .build();
    }
    
    
    
    
    
    //    public JsonObject sendVerificationEmailByEmail(String email) {
//        List<UserRepresentation> usersRepresentation = getUsersRepresentation(email);
//
//        if (usersRepresentation.isEmpty()) {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Eamil: ");
//            sb.append(email);
//            sb.append(" not exist.");
//            return json.buildErrorMessages(sb.toString(), null);
//        } else {
//            return sendVerificationEmailById(usersRepresentation.get(0).getId());
//        }
//    }
    
    
    
    
    
    
//    public JsonObject sendVerificationEmailById(String id) {
//        logger.info("sendVerificationEmailById : {}", id);
//
//        UserResource userResource = getUsersResource().get(id); 
//        userResource.sendVerifyEmail();
//        return json.converterUser(userResource.toRepresentation());
//    }

    
    
    


//    public JsonObject userAction(String jsonString, boolean isNew) {
//        logger.info("userAction");
//        
//        buildRealm(); 
//        
//        JsonObject dataJson = json.readInJson(jsonString).getJsonObject(CommonString.getInstance().getData());
//        JsonObject attributesJson = dataJson.getJsonObject(CommonString.getInstance().getAttributes());
//         
//        String firstName = attributesJson.getString(CommonString.getInstance().getFirstName());
//        String lastName = attributesJson.getString(CommonString.getInstance().getLastName());
//        String email = attributesJson.getString(CommonString.getInstance().getEmail()); 
//        String purpose = attributesJson.getString(CommonString.getInstance().getPurpose()); 
//        if(isNew) {
//            
//        } else {
//            String id = attributesJson.getString(CommonString.getInstance().getId());
//            UserResource userResource = keycloakClient.realm(CommonString.getInstance().getDinaRealm()).users().get(id);
//        }
//        
//        return null;
//    }
    //    private UserResource buildUserResource(UserRepresentation userRepresentation) {
//        return getDinaRealm().users().get(userRepresentation.getId());
//    }
//    private String resetTempPassword(UserResource userResource) {
//
//        String password = TempPasswordGenerator.generateRandomPassword();
//        CredentialRepresentation cred = new CredentialRepresentation();
//        cred.setType(CredentialRepresentation.PASSWORD);
//        cred.setValue(password);
//        cred.setTemporary(true);
//
//        userResource.resetPassword(cred);
//        return password;
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
