/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.utils;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author idali
 */
public class CommonString {
    
    private static CommonString instance = null;
    
    // keycloak constant 
    private static final String MASTER_REALM = "master";
    private static final String DINA_REALM = "dina";
    private static final String ADMIN_CLIENT_ID = "admin-cli";
    private static final String KEYCLOAK_URL = "http://localhost:8080/auth";

    private static final String MASTER_ADMIN_USERNAME = "admin";
    private static final String MASTER_ADMIN_PASSWORD = "dina";
    
    private static final String REGEX = ".*/(.*)$";
    private static final String REGEX_1 = "$1";
    private static final String DINA_REST_CLIENT_ID = "dina-rest";
    private static final String USER_MANAGEMENT_CLIENT_ID = "user-management";
    
    private static final String ADMIN_ROLE = "admin";
    
    private static final String LOCATION = "Location";
    // End of keycloak constant
    
    
 
    
    
    
    
    private final static String CALL_END_POINT = "callEndpoint";
    private final static String API_VERSION = "apiVersion";
    private final static String CALL_DATE = "callDate";  
    private final static String MAINTENANCE_CONTACT = "maintenanceContact";
    private final static String MAINTENANCE_CONTACT_VALUE = "admin@dina-system.org";
    private final static String CONTENT_LICENSES = "contentLicenses";
    private final static String LICENSES = "";
    private final static String RESULT_COUNT = "resultCount";
    private final static String STATUS_CODE = "statusCode";
    
    private final static String META = "meta";
    private final static String DATA = "data";
    private final static String TYPE = "type";
    private final static String ID = "id";
    private final static String ATTRIBUTES = "attributes";
    
    
    
    // Users
    private final static String TYPE_USERS = "users";
    private final static String FIRST_NAME = "first_name";
    private final static String LAST_NAME = "last_name";
    private final static String EMAIL = "email";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    
    private final static String ERRORS = "errors";
    private final static String SOURCE = "source";
    private final static String TITLE = "title";
    private final static String DETAIL = "detail";
     
    public static synchronized CommonString getInstance() {
        if (instance == null) {
            instance = new CommonString();
        }
        return instance;
    }
    
    
    
    public String getMastRealm() {
        return MASTER_REALM;
    }
    
    public String getDinaRealm() {
        return DINA_REALM;
    }
    
    public String getAdminClientId() {
        return ADMIN_CLIENT_ID;
    }
    
    public String getKeyCloakLUrl() {
        return KEYCLOAK_URL;
    }
    
    public String getMasterAdminUsrname() {
        return MASTER_ADMIN_USERNAME;
    }
    
    public String getMasterAdminPassword() {
        return MASTER_ADMIN_PASSWORD;
    }
    
    public String getREGEX() {
        return REGEX;
    }
    
    public String getREGEX1() {
        return REGEX_1;
    }
    
    public String getDinaRestClientId() {
        return DINA_REST_CLIENT_ID;
    }
    
    public String getUserManagementClientId() {
        return USER_MANAGEMENT_CLIENT_ID;
    }
    
    public String getLocation() {
        return LOCATION;
    }
    
    public String getAdminRole() {
        return ADMIN_ROLE;
    }
    
    public String getCallEndPoint() {
        return CALL_END_POINT;
    }
  
    public String getCallDate() {
        return CALL_DATE;
    }
    
    public String getEndPoint(String queryParam, String queryValue, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append("?");
        sb.append(queryParam);
        sb.append("=");
        sb.append(queryValue);
        return sb.toString();
    }
    
    
    public String getApiVersion() {
        return API_VERSION;
    }
    
    public String getApiVersionValue(String url) {
        String strVersion = StringUtils.substringBetween(url, "/v", "/search");
        return strVersion == null ? "01" : strVersion; 
    } 
     
    public String getMaintenanceContact() {
        return MAINTENANCE_CONTACT;
    }
    
    public String getMaintenanceContactValue() {
        return MAINTENANCE_CONTACT_VALUE;
    }
    
    public String getContentLicenses() {
        return CONTENT_LICENSES;
    }
    
    public String getLicenses() {
        return LICENSES;
    }
    
    public String getResultCount() {
        return RESULT_COUNT;
    }
    
    public String getStatusCode() {
        return STATUS_CODE;
    }
    
    public String getType() {
        return TYPE;
    }
    
    public String getId() {
        return ID;
    }
    
    public String getMeta() {
        return META;
    }
    
    public String getData() {
        return DATA;
    }
    
    public String getAttributes() {
        return ATTRIBUTES;
    }
    
    public String getUsersType() {
        return TYPE_USERS;
    }
     
    public String getErrors() {
        return ERRORS;
    }
    
    public String getSource() {
        return SOURCE;
    }
    
    public String getTitle() {
        return TITLE;
    }
    
    public String getDetail() {
        return DETAIL;
    }
    
    public String getFirstName() {
        return FIRST_NAME;
    }
    
    public String getLastName() {
        return LAST_NAME;
    }
    
    public String getEmail() {
        return EMAIL;
    }
    
    public String getUsername() {
        return USERNAME;
    }
}
