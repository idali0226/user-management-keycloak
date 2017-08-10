/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.json;

import java.util.List; 
import java.util.Map;
import javax.json.JsonObject;  
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

/**
 *
 * @author idali
 */
public interface JsonConverter {
     
    JsonObject readInJson(String json); 
    
    JsonObject converterUser(UserRepresentation userRepresentation, List<RoleRepresentation> realmRoles, Map<String, List<RoleRepresentation>> clientRoles);
    
    JsonObject converterUsers(List<UserRepresentation> userList);
    
    JsonObject converterRealm( RealmRepresentation realmRepresentation, List<RoleRepresentation> roleRepresentations, List<ClientRepresentation> clientRepresentations);
      
    JsonObject converterRole(RoleRepresentation roleRepresentation, String roleBelongTo);
    
    JsonObject converterClient(ClientRepresentation clienRepresentation, List<RoleRepresentation> roleRepresentations);
            
    JsonObject converterClients(Map<ClientRepresentation, List<RoleRepresentation>> clientRepresentationRolesMap);
    
    JsonObject successJson(String message);
    
    JsonObject buildErrorMessages(String error, List<String> errMsgs);
}
