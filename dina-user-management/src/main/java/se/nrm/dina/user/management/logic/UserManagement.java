/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;
 
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;   
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List; 
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
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
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.exceptions.UserManagementException;
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.logic.email.MailHandler;
import se.nrm.dina.user.management.utils.AccountStatus;
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
    public MailHandler mail;
    
    @Inject
    public JsonConverter json;
    
    
    
    public UserManagement() {  
    }
    
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
    
    public JsonObject createUser(String jsonString) {
        logger.info("createuser");
        
        buildRealm(); 
        
        JsonObject dataJson = json.readInJson(jsonString).getJsonObject(CommonString.getInstance().getData());
        JsonObject attributesJson = dataJson.getJsonObject(CommonString.getInstance().getAttributes());
         
        String firstName = attributesJson.getString(CommonString.getInstance().getFirstName());
        String lastName = attributesJson.getString(CommonString.getInstance().getLastName());
        String email = attributesJson.getString(CommonString.getInstance().getEmail()); 
        String purpose = attributesJson.getString(CommonString.getInstance().getPurpose()); 
//        String descriptions = attributesJson.getString(CommonString.getInstance().getDescriptions());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
          
        user.setEmail(email);
        user.setEnabled(true); 
        
        Map<String, List<String>> attributes = new HashMap<>();
        List<String> purposeList = new ArrayList<>();
        purposeList.add(purpose);
        attributes.put(CommonString.getInstance().getPurpose(), purposeList);
        
        List<String> statusList = new ArrayList<>();
        statusList.add(AccountStatus.New.getText());
        attributes.put(CommonString.getInstance().getStatus(), statusList);
        
        user.setAttributes(attributes);   
        
//        List<String> requirActions = new ArrayList();
//        requirActions.add("UPDATE_PASSWORD");
//        requirActions.add(("VERIFY_EMAIL"));
//        
//        user.setRequiredActions(requirActions);
        
  
        Response response = keycloakClient.realm(CommonString.getInstance().getDinaRealm()).users().create(user);
          
//        String locationHeader = response.getHeaderString(CommonString.getInstance().getLocation()); 
        response.close();
         
//        if (locationHeader != null) {  
//            UserResource userResource = getUsersResource()
//                                            .get(locationHeader
//                                                    .replaceAll(CommonString.getInstance().getREGEX(), 
//                                                                CommonString.getInstance().getREGEX1()));
//            userResource.sendVerifyEmail(); 
//        }
  
        UserRepresentation userRespresentation = keycloakClient.realm(CommonString.getInstance().getDinaRealm()).users().search(email, 0, 1).get(0); 
       
        keycloakClient.close();
        return json.converterUser(userRespresentation); 
    }
    
    public JsonObject sendVerificationEmail(String id, String email) {
        buildRealm(); 
        
        try {
            if(id != null && !id.isEmpty()) {
                return sendVerificationEmailById(id);
            } else if(email != null && !email.isEmpty()) {
                return sendVerificationByEmail(email);
            } else { 
                return json.buildErrorMessages("Bad request", null);
            }
        } catch(UserManagementException e) {
            return json.buildErrorMessages(e.getMessage(), null);
        } finally {
            if(keycloakClient != null) { 
                logger.info("keycloak client closed");
                keycloakClient.close();
            }  
        }
 
    } 
    
    private JsonObject sendVerificationByEmail(String email) { 
        List<UserRepresentation> usersRepresentation = getUsersRepresentation(email);
         
        if(usersRepresentation.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Eamil: ");
            sb.append(email);
            sb.append(" not exist.");
            throw new UserManagementException(400, email, sb.toString()); 
        } else {
            sendVerificationEmailById(usersRepresentation.get(0).getId());
        }  
        return json.successJson("Email has been sent successfully");
    }
    
    private JsonObject sendVerificationEmailById(String id) {
    //     buildRealm(); 
        
        UserResource userResource = getUsersResource().get(id); 
        userResource.sendVerifyEmail(); 
        return getUsers(CommonString.getInstance().getDinaRealm(), null);
    }
    
    public JsonObject updatePassword(String email) {
        logger.info("updatePassword : {}", email);
        
        buildRealm(); 
        List<UserRepresentation> userRepresentations = getUsersRepresentation(email);
        
        boolean hasError = false;
        if(userRepresentations.isEmpty()) {
            hasError = true; 
        } else {
            UserResource userResource = getUsersResource().get(userRepresentations.get(0).getId());
            userResource.resetPasswordEmail();
        }
        
        keycloakClient.close();
        if(hasError) {
            StringBuilder sb = new StringBuilder();
            sb.append("Eamil: ");
            sb.append(email);
            sb.append(" not exist.");
            return json.buildErrorMessages(sb.toString(), null);
        } 
        return json.successJson("Email sent successfully");
    }
    
 
    public JsonObject enableUser(String id) {
        buildRealm();
        UserResource userResource = getUsersResource().get(id);
          
        setRealmRole(userResource, CommonString.getInstance().getUserRole());
        setClientRole(userResource, CommonString.getInstance().getDinaRestClientId(), CommonString.getInstance().getUserRole());
        setClientRole(userResource, CommonString.getInstance().getUserManagementClientId(), CommonString.getInstance().getUserRole());

        UserRepresentation userRepresentation = userResource.toRepresentation();

        Map<String, List<String>> attributes = userRepresentation.getAttributes(); 
        attributes.remove("status");

        List<String> statusList = new ArrayList<>();
        statusList.add("existing");
        attributes.put("status", statusList);

        userRepresentation.setAttributes(attributes);
        userResource.update(userRepresentation);
        userResource.resetPasswordEmail();
        keycloakClient.close();
         
        return getUsers(CommonString.getInstance().getDinaRealm(), null);
    }

    public JsonObject updateUser(String jsonString) {
        buildRealm(); 
        
        JsonObject dataJson = json.readInJson(jsonString).getJsonObject(CommonString.getInstance().getData());
        JsonObject attributesJson = dataJson.getJsonObject(CommonString.getInstance().getAttributes());
         
        String firstName = attributesJson.getString(CommonString.getInstance().getFirstName());
        String lastName = attributesJson.getString(CommonString.getInstance().getLastName());
        String email = attributesJson.getString(CommonString.getInstance().getEmail()); 
        String purpose = attributesJson.getString(CommonString.getInstance().getPurpose()); 
        
        String id = dataJson.getString(CommonString.getInstance().getId());
        UserResource userResource = getUsersResource().get(id);
        
//        String password = attributesJson.getString(CommonString.getInstance().getPassword()); 
//        resetCredential(userResource, password);
        
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);
        userRepresentation.setEmail(email);
        userRepresentation.setUsername(email);
        userResource.update(userRepresentation);
         
        return getUsers(CommonString.getInstance().getDinaRealm(), email);
    }
    
    public JsonObject logout(String id) {
        logger.info("logout : {}", id);
        
        buildRealm();
        getDinaRealm().users().get(id).logout(); 
        keycloakClient.close();
        return json.successJson("User logout success");
    }
        
    private void setClientRole(UserResource userResource, String clientId, String roleName) {

        List<ClientRepresentation> crs = getClientsResource().findAll();
        ClientRepresentation cr = crs.stream()
                .filter(c -> c.getClientId().equals(clientId))
                .findFirst().get();

        String cId = cr.getId();

        List<RoleRepresentation> newRole = new ArrayList<>();
        List<RoleRepresentation> clrs = getClientsResource().get(cId).roles().list();
        clrs.stream()
                .forEach(rr -> {
                    if (rr.getName().equals(roleName)) {
                        newRole.add(rr);
                    }
                });
        userResource.roles().clientLevel(cId).add(newRole);
    }

    private void setRealmRole(UserResource userResource, String role) {
        
        
        List<RoleRepresentation> rrs = userResource.roles().realmLevel().listAll();
        userResource.roles().realmLevel().remove(rrs);
        
        List<RoleRepresentation> dinaRealmRoles = getDinaRealmResource().roles().list();

        List<RoleRepresentation> newRole = new ArrayList<>();
        dinaRealmRoles.stream()
                        .filter(drr -> !drr.getName().equals(CommonString.getInstance().getOfflineAccessRole()))
                        .filter(drr -> !drr.getName().equals(CommonString.getInstance().getUmaAuthorizationRole()))
                        .forEach(drr -> { 
                            if(drr.getName().equals(role)) {
                                newRole.add(drr);
                            } 
                        });  
        userResource.roles().realmLevel().add(newRole);  
    }

    private ClientsResource getClientsResource() {
        return getDinaRealmResource().clients();
    }
    
    private List<UserRepresentation> getUsersRepresentation(String email) {
        UsersResource usersResource = getUsersResource();
        return usersResource.search(email, 0, usersResource.count());
    }

    private UsersResource getUsersResource() {
        return getDinaRealmResource().users();
    }

    private RealmResource getDinaRealmResource() {
        return keycloakClient.realm(CommonString.getInstance().getDinaRealm());
    }

    private void resetCredential(UserResource userResource, String password) {
//        String password = TempPasswordGenerator.generateRandomPassword();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
         
        boolean isTempPassword = false;
        if(password == null) {
            password = TempPasswordGenerator.generateRandomPassword();
            isTempPassword = true;
        }
        cred.setValue(password);
        cred.setTemporary(isTempPassword);

        userResource.resetPassword(cred);
    }

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
    
    public JsonObject getUserById(String realm, String id) {
        logger.info("getUserById");
        buildRealm();
        
        UserRepresentation userRepresentation = getUsersResource().get(id).toRepresentation();
        keycloakClient.close();
        return json.converterUser(userRepresentation);
    }
    
    

    public JsonObject getUsers(String realm, String userName) {
        logger.info("getUsers");
        buildRealm(); 
        
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> list = usersResource.search(userName, 0, usersResource.count()); 
 
//        RealmRepresentation realmRepresentation = keycloakClient.realms().realm(CommonString.getInstance().getDinaRealm()).toRepresentation();
//        String passwordPolicy = realmRepresentation.getPasswordPolicy();
//        logger.info("password policy: {}", passwordPolicy);
 
//        list.stream()
//                .forEach(u -> {
//                    logger.info("required actions : {}", u.getRequiredActions());
//                    u.getRequiredActions(); 
//                }); 
         
        keycloakClient.close();
        return json.converterUsers(list);
    }
    
    
    public JsonObject getLoggedInUser() {
        logger.info("getLoggedInUser");
        buildRealm(); 
        UsersResource usersResource = getDinaRealm().users();
        int count = usersResource.count();
        List<UserRepresentation> list = usersResource.search(null, 0, count); 
        List<UserRepresentation> loggedInUsers = new ArrayList();
        
        list.stream()
            .forEach(l -> {
                UserResource userResource = getDinaRealm().users().get(l.getId());
                if(!userResource.getUserSessions().isEmpty()) {
                    loggedInUsers.add(l);
                }
            });
                
        return json.converterUsers(loggedInUsers);
    }
    
    private UserResource buildUserResource(UserRepresentation userRepresentation) {
        return getDinaRealm().users().get(userRepresentation.getId());
    }
    
    
    
    
    private RealmResource getDinaRealm() {
       return keycloakClient.realm(CommonString.getInstance().getDinaRealm());
    }
    

    private void buildRealm() {

        String keycloakAuthURL;
        Properties prop = new Properties();
        InputStream input = null;
        try {
            prop.load(new FileInputStream(CommonString.getInstance().getConfigProperties())); 
            keycloakAuthURL = prop.getProperty(CommonString.getInstance().getKeycloakAuthURL());
//            String keycloakAuthURL = Util.getInstance().getPropertyValue(CommonString.getInstance().getKeyCloakLUrl());
            
            logger.info("keycloak url : {}", keycloakAuthURL);
//            realmName = prop.getProperty(CommonString.getInstance().getRealmName());
            keycloakClient = KeycloakBuilder.builder()
                    .serverUrl(keycloakAuthURL) //
                    .realm(CommonString.getInstance().getMastRealm())//
                    .username(CommonString.getInstance().getMasterAdminUsrname()) //
                    .password(CommonString.getInstance().getMasterAdminPassword()) //
                    .clientId(CommonString.getInstance().getAdminClientId())
                    .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
                    .build();

        } catch (IOException ex) {
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
