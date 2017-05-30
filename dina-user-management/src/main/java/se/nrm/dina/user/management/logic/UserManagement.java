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
    
    
    
    public JsonObject createUser(String jsonString) {
        logger.info("createuser");
        
        buildRealm(); 
        
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
        user.setEnabled(true); 
        
        Map<String, List<String>> attributes = new HashMap<>();
        List<String> purposeList = new ArrayList<>();
        purposeList.add(purpose);
        attributes.put(CommonString.getInstance().getPurpose(), purposeList);
        
        List<String> statusList = new ArrayList<>();
        statusList.add(AccountStatus.New.getText());
        attributes.put(CommonString.getInstance().getStatus(), statusList);
        
        user.setAttributes(attributes);   
        Response response = keycloakClient.realm(CommonString.getInstance().getDinaRealm()).users().create(user);
        
//        List<String> actions = new ArrayList();
//        actions.add("Verify Email");
        
         
  
//        String locationHeader = response.getHeaderString(CommonString.getInstance().getLocation()); 
        response.close();
        
        
        
        
//        String password;
//        if (locationHeader != null) {  
//            UserResource userResource = getUsersResource()
//                                            .get(locationHeader
//                                                    .replaceAll(CommonString.getInstance().getREGEX(), 
//                                                                CommonString.getInstance().getREGEX1()));
            
//            userResource.sendVerifyEmail();
//            userResource.executeActionsEmail(CommonString.getInstance().getUserManagementClientId(), "http://localhost:4200/login",  requiredActions);
//            password = resetTempPassword(userResource);   
//            mail.sendMail(email, password, firstName, lastName);
//        }  
        
//        keycloakClient.close(); 
        
        UserRepresentation userRespresentation = keycloakClient.realm(CommonString.getInstance().getDinaRealm()).users().search(email, 0, 1).get(0); 
        return json.converterUser(userRespresentation); 
    }
    
    public JsonObject sendVerificationEmail(String id) {
        buildRealm(); 
        UserResource userResource = getUsersResource().get(id); 
        userResource.sendVerifyEmail();
         
        keycloakClient.close();
        return getUsers(CommonString.getInstance().getDinaRealm(), null);
    }

    public JsonObject enableUser(String id) {
        buildRealm();
        UserResource userResource = getUsersResource().get(id);
        resetCredential(userResource);

        setRealmRole(userResource, CommonString.getInstance().getUserRole());
        setClientRole(userResource, CommonString.getInstance().getDinaRestClientId(), CommonString.getInstance().getUserRole());
        setClientRole(userResource, CommonString.getInstance().getUserManagementClientId(), CommonString.getInstance().getDataEntryRole());

        UserRepresentation userRepresentation = userResource.toRepresentation();

        Map<String, List<String>> attributes = userRepresentation.getAttributes();

        logger.info("map : {}", attributes);
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
        List<RoleRepresentation> dinaRealmRoles = getDinaRealmResource().roles().list();

        List<RoleRepresentation> newRole = new ArrayList<>();
        dinaRealmRoles.stream()
                .forEach(drr -> {
                    if (drr.getName().equals(role)) {
                        newRole.add(drr);
                    }
                });
        userResource.roles().realmLevel().add(newRole);
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

    private void resetCredential(UserResource userResource) {
        String password = TempPasswordGenerator.generateRandomPassword();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(true);

        userResource.resetPassword(cred);
    }

    private String resetTempPassword(UserResource userResource) {

        String password = TempPasswordGenerator.generateRandomPassword();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(true);

        userResource.resetPassword(cred);
        return password;
    }

    public JsonObject getUsers(String realm, String userName) {
        logger.info("getUsers");
        buildRealm();
        List<UserRepresentation> list = keycloakClient.realm(realm).users().search(userName, 0, 100);
        keycloakClient.close();
        return json.converterUsers(list);
    }

    private void buildRealm() {

        String keycloakAuthURL;
        Properties prop = new Properties();
        InputStream input = null;
        try {
            prop.load(new FileInputStream(CommonString.getInstance().getConfigProperties()));
            keycloakAuthURL = prop.getProperty(CommonString.getInstance().getKeycloakAuthURL());
            
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
