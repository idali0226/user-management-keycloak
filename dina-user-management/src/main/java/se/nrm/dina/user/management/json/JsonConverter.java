/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.json;

import java.util.List; 
import javax.json.JsonObject;
import org.keycloak.representations.idm.UserRepresentation;

/**
 *
 * @author idali
 */
public interface JsonConverter {
     
    JsonObject readInJson(String json);
    
    JsonObject converterUsers(List<UserRepresentation> userList);
    
}
