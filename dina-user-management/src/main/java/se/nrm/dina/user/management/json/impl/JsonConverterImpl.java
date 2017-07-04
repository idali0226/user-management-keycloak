/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.json.impl;

import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Date; 
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader; 
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.json.JsonConverter;
import se.nrm.dina.user.management.utils.CommonString;
import se.nrm.dina.user.management.utils.Util;

/**
 *
 * @author idali
 */
public class JsonConverterImpl implements Serializable, JsonConverter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(null);

    public JsonConverterImpl() {

    }
    
    @Override
    public JsonObject converterRole(RoleRepresentation roleRepresentation) {   
        
        JsonObjectBuilder jsonBuilder = JSON_FACTORY.createObjectBuilder();
        JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
        buildRoleJson(roleRepresentation, dataBuilder, true);

        jsonBuilder.add(CommonString.getInstance().getData(), dataBuilder);
        return jsonBuilder.build(); 
    }

    @Override
    public JsonObject converterClients(Map<ClientRepresentation, List<RoleRepresentation>> clientRepresentationRolesMap) {

        JsonObjectBuilder jsonBuilder = JSON_FACTORY.createObjectBuilder();
        JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
        JsonArrayBuilder dataArrBuilder = Json.createArrayBuilder();
        
        JsonObjectBuilder relBuilder = Json.createObjectBuilder();

        if (clientRepresentationRolesMap != null && !clientRepresentationRolesMap.isEmpty()) {
            clientRepresentationRolesMap.entrySet().stream()
                    .forEach(c -> {
                        buildClientData(c.getKey(), dataBuilder);
                        buildClientRoles(c.getValue(), relBuilder);
                        dataBuilder.add("relationships", relBuilder); 
                        dataArrBuilder.add(dataBuilder);  
                    }); 
        }

        jsonBuilder.add(CommonString.getInstance().getData(), dataArrBuilder);
        return jsonBuilder.build();
    }
    
    private void buildClientRoles(List<RoleRepresentation> roleRepresentations, JsonObjectBuilder relBuilder) {
         
        JsonObjectBuilder subBuilder = Json.createObjectBuilder();  
        JsonObjectBuilder subDataBuilder = Json.createObjectBuilder();  
        JsonArrayBuilder subDataArrBuilder = Json.createArrayBuilder();
         
        if (roleRepresentations != null && !roleRepresentations.isEmpty()) {
     
            roleRepresentations.stream()
                                .forEach(r -> {  
                                    buildRoleJson(r, subDataBuilder, false);
                                    subDataArrBuilder.add(subDataBuilder);
                                }); 
            subBuilder.add(CommonString.getInstance().getData(), subDataArrBuilder);
            relBuilder.add("roles", subBuilder);  
        }
    }
    
    private void buildRoleJson(RoleRepresentation roleRepresentation, JsonObjectBuilder subDataBuilder, boolean addAttributes) { 
        subDataBuilder.add(CommonString.getInstance().getType(), CommonString.getInstance().getTypeRoles());
        subDataBuilder.add(CommonString.getInstance().getId(), roleRepresentation.getId()); 
        
        if(addAttributes) {
            JsonObjectBuilder attBuilder = Json.createObjectBuilder(); 
            addAttributes(attBuilder, "role_name", roleRepresentation.getName()); 
            addAttributes(attBuilder, "descriptions", roleRepresentation.getDescription());
             
            subDataBuilder.add(CommonString.getInstance().getAttributes(), attBuilder);
        }
    }

  
    @Override
    public JsonObject converterRoles(RolesResource realmRoles) {
        
        JsonObjectBuilder jsonBuilder = JSON_FACTORY.createObjectBuilder();
         
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JsonObject readInJson(String json) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(json))) {
            return jsonReader.readObject();
        }
    }

    @Override
    public JsonObject converterUser(UserRepresentation userRepresentation) {
        logger.info("converterUser");

        JsonObjectBuilder jsonBuilder = JSON_FACTORY.createObjectBuilder();
        JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
        buildUserData(userRepresentation, dataBuilder);

        jsonBuilder.add(CommonString.getInstance().getData(), dataBuilder);
        return jsonBuilder.build();
    }

    @Override
    public JsonObject converterUsers(List<UserRepresentation> userList) {

        JsonObjectBuilder jsonBuilder = JSON_FACTORY.createObjectBuilder();
        JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
        JsonArrayBuilder dataArrBuilder = Json.createArrayBuilder();

        if (userList != null && !userList.isEmpty()) {
            userList.stream()
                    .forEach(u -> {
                        buildUserData(u, dataBuilder);
                        dataArrBuilder.add(dataBuilder);
                    });
        }

        jsonBuilder.add(CommonString.getInstance().getData(), dataArrBuilder);
        return jsonBuilder.build();
    }
    
    @Override
    public JsonObject successJson(String message) {
        JsonObjectBuilder jsonBuilder = JSON_FACTORY.createObjectBuilder();
        jsonBuilder.add("response", message);
        return jsonBuilder.build(); 
    }

    private void buildClientData(ClientRepresentation clientRepresentation, JsonObjectBuilder dataBuilder) {

        JsonObjectBuilder attBuilder = Json.createObjectBuilder();

        dataBuilder.add(CommonString.getInstance().getType(), CommonString.getInstance().getClientsType());
        dataBuilder.add(CommonString.getInstance().getId(), clientRepresentation.getId());
       
        addAttributes(attBuilder, "client_name", clientRepresentation.getName());
        addAttributes(attBuilder, "client_id", clientRepresentation.getClientId());
        addAttributes(attBuilder, "descriptions", clientRepresentation.getDescription());
          
        dataBuilder.add(CommonString.getInstance().getAttributes(), attBuilder);
    }
     
    private void buildUserData(UserRepresentation userRepresentation, JsonObjectBuilder dataBuilder) {

        JsonObjectBuilder attBuilder = Json.createObjectBuilder();

        dataBuilder.add(CommonString.getInstance().getType(), CommonString.getInstance().getUsersType());
        dataBuilder.add(CommonString.getInstance().getId(), userRepresentation.getId());

        addAttributes(attBuilder, CommonString.getInstance().getFirstName(), userRepresentation.getFirstName());
        addAttributes(attBuilder, CommonString.getInstance().getLastName(), userRepresentation.getLastName());
        addAttributes(attBuilder, CommonString.getInstance().getEmail(), userRepresentation.getEmail());
        addAttributes(attBuilder, CommonString.getInstance().getUsername(), userRepresentation.getUsername());
        addAttributes(attBuilder, CommonString.getInstance().getTimestampCreated(), userRepresentation.getCreatedTimestamp());
        addAttributes(attBuilder, CommonString.getInstance().isUserEnabled(), userRepresentation.isEnabled());
        addAttributes(attBuilder, CommonString.getInstance().isEmailVerified(), userRepresentation.isEmailVerified());

        Map<String, List<String>> attrs = userRepresentation.getAttributes();
        if(attrs != null && !attrs.isEmpty()) {
            List<String> purposes = attrs.get(CommonString.getInstance().getPurpose());
            if (purposes != null && !purposes.isEmpty()) {
                addAttributes(attBuilder, CommonString.getInstance().getPurpose(), purposes.get(0));
            }
            List<String> status = attrs.get(CommonString.getInstance().getStatus());
            if (status != null && !status.isEmpty()) {
                addAttributes(attBuilder, CommonString.getInstance().getStatus(), status.get(0));
            }
            
//            List<String> descriptions = attrs.get(CommonString.getInstance().getDescriptions());
//            if(descriptions != null && !descriptions.isEmpty()) {
//                addAttributes(attBuilder, CommonString.getInstance().getDescriptions(), descriptions);
//            }
        }
         
        dataBuilder.add(CommonString.getInstance().getAttributes(), attBuilder);
    }

    private void addAttributes(JsonObjectBuilder attBuilder, String key, Object value) {

        if (key != null && value != null) {
            if (value instanceof Integer) {
                attBuilder.add(key, (int) value);
            } else if (value instanceof Short) {
                attBuilder.add(key, (Short) value);
            } else if (value instanceof Date) {
                attBuilder.add(key, Util.getInstance().dateToString((Date) value));
            } else if (value instanceof java.util.Date) {
                attBuilder.add(key, Util.getInstance().dateToString((java.util.Date) value));
            } else if (value instanceof BigDecimal) {
                attBuilder.add(key, (BigDecimal) value);
            } else if (value instanceof Boolean) {
                attBuilder.add(key, (Boolean) value);
            } else if (value instanceof Double) {
                attBuilder.add(key, (Double) value);
            } else if (value instanceof Float) {
                attBuilder.add(key, (Float) value);
            } else if (value instanceof Long) {
                attBuilder.add(key, (Long) value);
            } else if(value instanceof java.util.LinkedList) {
                attBuilder.add(key, value.toString());
            } else {
                attBuilder.add(key, (String) value);
            }
        } 
    } 
}
