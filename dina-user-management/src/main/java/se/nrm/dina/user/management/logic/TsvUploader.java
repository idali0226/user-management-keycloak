/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings; 
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable; 
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.List; 
import java.util.Map;   
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils; 
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author idali
 */ 
public class TsvUploader implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final String MASTER_REALM = "master";
    private static final String DINA_REALM = "dina";
    private static final String ADMIN_REALM = "admin-cli";
    private static final String URL = "http://localhost:8080/auth";

    private static final String MASTER_ADMIN_USERNAME = "admin";
    private static final String MASTER_ADMIN_PASSWORD = "dina";
    
    private static final String REGEX = ".*/(.*)$";
    private static final String CLIENT_END_POINT = "dina-rest";
    private static final String CLIENT_ID = "user-management";
    
    private static final String ADMIN_ROLE = "admin";

    private Map<String, List<String>> attributes;

//    private String specifyUserId;
    private String email;
    private String username;
    private String password;
//    private String agentId;

    private Keycloak kc;

    public TsvUploader() {

    }
    
    public void test() {
        logger.info("test");
    }

    public void upload(String tsvPath) {
        buildRealm();
        List<String[]> list = readInTsvFile(tsvPath);

        list.remove(0);
        list.stream()
                .forEach((String[] r) -> {
                    readValueFromRow(r);
                    setAttributes();

                    UserRepresentation user = new UserRepresentation();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setEnabled(true); 
                    user.setAttributes(attributes);

                    Response response = kc.realm(DINA_REALM).users().create(user);

                    String locationHeader = response.getHeaderString("Location");
                    response.close();
 
                    if (locationHeader != null) { 
                        UserResource userResource = kc.realm(DINA_REALM).users().get(locationHeader.replaceAll(REGEX, "$1"));
                        resetPassword(userResource);
                        setReamlRole(userResource);
                        setClientRole(CLIENT_END_POINT, userResource);
                        setClientRole(CLIENT_ID, userResource);
                    }
                }); 
        
        kc.close();
    }
    
    private void setClientRole(String clientId, UserResource userResource) {

        List<ClientRepresentation> crs = kc.realm(DINA_REALM).clients().findAll();
        ClientRepresentation cr = crs.stream()
                                     .filter(c -> c.getClientId().equals(clientId))
                                     .findFirst().get();
        
        String cId = cr.getId();

        List<RoleRepresentation> clrs = kc.realm(DINA_REALM).clients().get(cId).roles().list();
        clrs.stream()
                .forEach(rr -> {
                    if (rr.getName().equals(ADMIN_ROLE)) {
                        userResource.roles().clientLevel(cId).add(clrs);
                    }
                }); 
    }

    private void setReamlRole(UserResource userResource) {
        List<RoleRepresentation> dinaRealmRoles = kc.realm(DINA_REALM).roles().list();

        List<RoleRepresentation> newRole = new ArrayList<>();
        dinaRealmRoles.stream()
                        .forEach(drr -> { 
                            if(drr.getName().equals(ADMIN_ROLE)) {
                                newRole.add(drr);
                            }
                            userResource.roles().realmLevel().add(newRole);
                        }); 
    }
    
    private void resetPassword(UserResource userResource) {

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);

        userResource.resetPassword(cred);
    }
    
    
    private void setAttributes() {

        attributes = new HashMap<>();
        List<String> attList = new ArrayList<>();
        attList.add(username);
        attributes.put("userid", attList); 
    }
      
    
    private void readValueFromRow(String[] row) { 
        email = StringUtils.substringBetween(row[2], "\"", "\"");
        username = StringUtils.substringBetween(row[3].trim(), "\"", "\"");
        password = StringUtils.substringBetween(row[5].trim(), "\"", "\"");
    }
    
    private void buildRealm() {   
        kc = KeycloakBuilder.builder()
                .serverUrl(URL) //
                .realm(MASTER_REALM)//
                .username(MASTER_ADMIN_USERNAME) //
                .password(MASTER_ADMIN_PASSWORD) //
                .clientId(ADMIN_REALM)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
                .build();
    }
    
    private List<String[]> readInTsvFile(String tsvPath) {
        Reader fileReader = null;
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
         
        try {
            TsvParser parser = new TsvParser(settings);

            fileReader = new FileReader(tsvPath);
            return parser.parseAll(fileReader);
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException ex) {
            }
        }
        return null; 
    } 
}
