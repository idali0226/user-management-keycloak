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
    
    private final String ENV_KEYCLOAK_URI = "KEYCLOAK_URI";
    private final String ENV_REALM_NAME = "REALM_NAME";
    
    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final String INITIAL_SETUP = "initialSetup";
    
    // keycloak constant  
    private static final String MASTER_REALM = "master"; 
    private static final String ADMIN_CLIENT_ID = "admin-cli"; 
    
    private static final String KEYCLOAK_AUTH_URL = "keycloakAuthURL";

    private static final String MASTER_ADMIN_USERNAME = "admin";
    private static final String MASTER_ADMIN_PASSWORD = "dina";
     
    private static final String REGEX = ".*/(.*)$";
    private static final String REGEX_1 = "$1";

    private static final String SUPER_ADMIN_ROLE = "super admin";
    private static final String ADMIN_ROLE = "admin";
    private static final String USER_ROLE = "user";
    private static final String DATA_ENTRY_ROLE = "data entry";
    private static final String DISABLED_USER_ROLE = "disabled_user";
    
    private static final String ADMIN_ROLE_DESCRIPTION = "A user has admin role can manage user accounts, clients in User management application. ";
    private static final String USER_ROLE_DESCRIPTION = "A user has user role can only view and edit his/her own account.";
    private static final String DISABLED_ROLE_DESCRIPTION = "A user has disabled role can not access any resource in DINA";
    
    private static final String REALM_ROLE = "realm_role";
    
    private static final String LOCATION = "Location";
    
    private static final String CLIENT_NAME = "claim.name";
    // End of keycloak constant
    
    // Realm
    private static final String REALM_NAME = "realmName";
    private static final String SUPER_USERNAME = "superUsername";
    private static final String SUPER_PASSWORD = "superPassword";
    private static final String SUPER_USER_FIRST_NAME = "Admin";
    private static final String SUPER_USER_LAST_NAME = "Admin";
    // End of realm
    
    // Client
    private static final String DINA_REST_CLIENT_ID = "dina-rest";
    private static final String DINA_REST_CLIENT_NAME = "dina rest endpoint";
    private static final String DINA_REST_CLIENT_DESCRIPTION = "Provides an entry point to authenticate user into DINA system.";
    private static final String USER_MANAGEMENT_CLIENT_ID = "user-management"; 
    private static final String USER_MANAGEMENT_CLIENT_NAME = "User management";
    private static final String USER_MANAGEMENT_CLIENT_DESCRIPTION = "Management user accounts in keycloak";
    private static final String OPEN_ID_CONNECT_PROTOCOL = "openid-connect";
    // End of client
    
    
    // Mail server 
    private static final String MAIL_SERVER_HOST = "mailServerHost";
    private static final String MAIL_SERVER_PORT = "mailServerPort";
    private static final String MAIL_SERVER_USER = "mailServerUsername";
    private static final String MAIL_SERVER_PASSWORD = "mailServerPassword"; 
    // End of mail server
     
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
    
    
    private final static String PURPOSE = "purpose";
    private final static String DESCRIPTIONS = "descriptions";
    private final static String TIME_CREATED = "timestamp_created";
    private final static String IS_USER_ENABLED = "is_enabled";
    private final static String IS_EMAIL_VERIFIED = "is_email_verified";
    
    private final static String ERRORS = "errors";
    private final static String SOURCE = "source";
    private final static String TITLE = "title";
    private final static String DETAIL = "detail";
    
    private final static String TYPE_CLIENTS = "clients";
    private final static String MANAGEMENT_REALM = "ManagementRealm";
    
    private final static String TYPE_REALM = "realms";
    private final static String TYPE_ROLE = "role";
    
    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String FROM = "from";
    private final static String SSL = "ssl";
    private final static String STRT_TTLS = "starttls";
    private final static String AUTH = "auth";
    
    private final static String USER = "user";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
      
    private final static String STATUS  = "status";
    
    private final static String QUESTION_MARK = "?";
    private final static String EQUAL_SIGN = "=";
    
    private final String MULTI_VALUED = "multivalued";
    private final String USER_INFO_TOKEN_CLAIM = "userinfo.token.claim";
    private final String USER_ATTRIBUTE = "user.attribute";
    private final String ID_TOKEN_CLAIM = "id.token.claim";
    private final String ACCESS_TOKEN_CLAIM = "access.token.claim";
    private final String CLAIM_TOKEN_NAME = "claim.token.name";
    private final String JSON_TYPE_LABEL = "jsonType.label";
    private final String STRING_DATA_FORMAT = "String";
    
    private final String REALM_ROLE_CLAIM_NAME = "realm_role";
    private final String PREFERRED_USERNAME = "preferred_username";
    
    private final String PROTOCOL_MAPPER_REALM_ROLE_MAPPER = "oidc-usermodel-realm-role-mapper";
    private final String PROTOCOL_MAPPER_PROPERTY_MAPPER = "oidc-usermodel-property-mapper";
    
    private final String TOKEN_FIRST_NAME = "firstName";
    private final String TOKEN_GIVEN_NAME = "given_name";
    private final String TOKEN_LAST_NAME = "lastName";
    private final String TOKEN_FAMILY_NAME = "family_name";
      
    private final String PROTOCOL_MAPPER_CONSENT_TEXT_REALM_ROLE = "${realmRole}"; 
    private final String PROTOCOL_MAPPER_CONSENT_TEXT_EMAIL = "${email}"; 
    private final String PROTOCOL_MAPPER_CONSENT_TEXT_USERNAME = "${username}";
     
    private final String PROTOCOL_MAPPER_NAME_FALIMY_NAME = "family name";
    private final String PROTOCOL_MAPPER_CONSENT_TEXT_FALIMY_NAME = "${familyName}";
    
    private final String PROTOCOL_MAPPER_NAME_GIVEN_NAME = "given name";
    private final String PROTOCOL_MAPPER_CONSENT_TEXT_GIVEN_NAME = "${givenName}";
    
    private final String OFFLINE_ACCESS_ROLE = "offline_access";
    private final String UMA_AUTHORIZATION_ROLE = "uma_authorization";
    
    private final String TYPE_ROLES = "roles";
    
    private final String RELATIONSHIPS = "relationships";
    
    
    private final String REDIRECT_FRONT_END_URL = "http://localhost:4200";
    
//    private final String REDIRECT_FRONT_END_URL_VIRTUALHOST = "http://beta-accounts.dina-web.net";
    
    private final String NONE = "none";
    
    public static synchronized CommonString getInstance() {
        if (instance == null) {
            instance = new CommonString();
        }
        return instance;
    }
    
    public String getEnvKeycloakURI() {
        return ENV_KEYCLOAK_URI;
    }
    
    public String getEnvRealmName() {
        return ENV_REALM_NAME;
    }
    
    public String getInitialSetup() {
        return INITIAL_SETUP;
    }
    
    public String getUmaAuthorizationRole() {
        return UMA_AUTHORIZATION_ROLE;
    }
    
    public String getOfflineAccessRole() {
        return OFFLINE_ACCESS_ROLE;
    }
    
    public String getProtocolMapperConsentTextGivenName() {
        return PROTOCOL_MAPPER_CONSENT_TEXT_GIVEN_NAME;
    }
    
    public String getProtocolMapperNameGivenName() {
        return PROTOCOL_MAPPER_NAME_GIVEN_NAME;
    }
    
    public String getProtocolMapperConsentTextFamilyName() {
        return PROTOCOL_MAPPER_CONSENT_TEXT_FALIMY_NAME;
    }
    
    public String getProtocolMapperNameFalimyName() {
        return PROTOCOL_MAPPER_NAME_FALIMY_NAME;
    }
    
    public String getProtocolMapperConsentTextUsername() {
        return PROTOCOL_MAPPER_CONSENT_TEXT_USERNAME;
    }
    
    public String getProtocolMapperConsentTextEmail() {
        return PROTOCOL_MAPPER_CONSENT_TEXT_EMAIL;
    }
    
    public String getProtocolMapperConsentTextRealmRole() {
        return PROTOCOL_MAPPER_CONSENT_TEXT_REALM_ROLE;
    }
    
    public String getNone() {
        return NONE;
    }
    
    public String getRedirectFrontEndURL() {
        return REDIRECT_FRONT_END_URL;
    }
    
//    public String getRedirectFrontEndURLVirturalHost() {
//        return REDIRECT_FRONT_END_URL_VIRTUALHOST;
//    }
    
    public String getTokenFirstName() {
        return TOKEN_FIRST_NAME;
    }
    
    public String getTokenLastName() {
        return TOKEN_LAST_NAME;
    }
    
    public String getTokenGivenName() {
        return TOKEN_GIVEN_NAME;
    }
    
    public String getTokenFamilyName() {
        return TOKEN_FAMILY_NAME;
    }
    
    public String getPreferredUsername() {
        return PREFERRED_USERNAME;
    }
    
    public String getProtocolMapperRealmRoleMapper() {
        return PROTOCOL_MAPPER_REALM_ROLE_MAPPER;
    }
    
    public String getProtocolMapperPropertyMapper() {
        return PROTOCOL_MAPPER_PROPERTY_MAPPER;
    }
    
    public String getRealmRoleClaimName() {
        return REALM_ROLE_CLAIM_NAME;
    }
    
    public String getStringDataFormat() {
        return STRING_DATA_FORMAT;
    }
    
    public String getMultiValued() {
        return MULTI_VALUED;
    }
    
    public String getUserInfoTokenClaim() {
        return USER_INFO_TOKEN_CLAIM;
    }
    
    public String getUserAttribute() {
        return USER_ATTRIBUTE;
    }
    
    public String getIdTokenClaim() {
        return ID_TOKEN_CLAIM;
    }
    
    public String getAccessTokenClaim() {
        return ACCESS_TOKEN_CLAIM;
    }
    
    public String getClaimTokenName() {
        return CLAIM_TOKEN_NAME;
    }
    
    public String getJsonTypeLabel() {
        return JSON_TYPE_LABEL;
    }
    
    public String getOpenIdConnectionProtocol() {
        return OPEN_ID_CONNECT_PROTOCOL;
    }
    
    public String getStatus() {
        return STATUS;
    }
    
    public String getSuperUserFirstName() {
        return SUPER_USER_FIRST_NAME;
    }
    
    public String getSuperUserLastName() {
        return SUPER_USER_LAST_NAME;
    }
    
    public String getUser() {
        return USER;
    }
    
    public String getPassword() {
        return PASSWORD;
    }
    
    public String getAuth() {
        return AUTH;
    }
    
    public String getSSL() {
        return SSL;
    }
    
    public String getStrtTTLS() {
        return STRT_TTLS;
    }
    
    public String getFrom() {
        return FROM;
    }
    
    public String getHost() {
        return HOST;
    }
    
    public String getPort() {
        return PORT;
    }
    
    public String getKeycloakAuthURL() {
        return KEYCLOAK_AUTH_URL;
    }
    
    public String getManagementRealm() {
        return MANAGEMENT_REALM;
    }
    
    public String getConfigProperties() {
        return CONFIG_PROPERTIES;
    }
    
    public String getRealmName() {
        return REALM_NAME;
    }
    
    public String getSuperUsername() {
        return SUPER_USERNAME;
    }
    
    public String getSuperPassword() {
        return SUPER_PASSWORD;
    }
    
    public String getMailServerHost() {
        return MAIL_SERVER_HOST;
    }
    
    public String getMailServerPort() {
        return MAIL_SERVER_PORT;
    }
    
    public String getMailServerUsername() {
        return MAIL_SERVER_USER;
    }
    
    public String getMailServerPassword() {
        return MAIL_SERVER_PASSWORD;
    }
    
    public String getMastRealm() {
        return MASTER_REALM;
    }
 
    public String getAdminClientId() {
        return ADMIN_CLIENT_ID;
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
    
    public String getDinaRestClientName() {
        return DINA_REST_CLIENT_NAME;
    }
    
    public String getDinaRestClientDescription() {
        return DINA_REST_CLIENT_DESCRIPTION;
    }
    
    public String getUserManagementClientName() {
        return USER_MANAGEMENT_CLIENT_NAME;
    }
    
    public String getUserManagementClientId() {
        return USER_MANAGEMENT_CLIENT_ID;
    }
    
    public String getUserManagementClientDescription() {
        return USER_MANAGEMENT_CLIENT_DESCRIPTION;
    }
    
    public String isUserEnabled() {
        return IS_USER_ENABLED;
    }
    
    public String isEmailVerified() {
        return IS_EMAIL_VERIFIED;
    }
    
    public String getLocation() {
        return LOCATION;
    }
    
    public String getSuperAdminRole() {
        return SUPER_ADMIN_ROLE;
    }
    
    public String getAdminRole() {
        return ADMIN_ROLE;
    }
    
    public String getUserRole() {
        return USER_ROLE;
    }
    
    public String getDataEntryRole() {
        return DATA_ENTRY_ROLE;
    }
    
    public String getDisabledUserRole() {
        return DISABLED_USER_ROLE;
    }
    
    public String getAdminRoleDescription() {
        return ADMIN_ROLE_DESCRIPTION;
    }
    
    public String getUserRoleDescription() {
        return USER_ROLE_DESCRIPTION;
    }
    
    public String getDisabledRoleDescription() {
        return DISABLED_ROLE_DESCRIPTION;
    }
    
    public String getCallEndPoint() {
        return CALL_END_POINT;
    }
  
    public String getCallDate() {
        return CALL_DATE;
    }
    
    public String getTypeClients() {
        return TYPE_CLIENTS;
    }
    
    public String getClientName() {
        return CLIENT_NAME;
    }
    
    public String getRealmType() {
        return TYPE_REALM;
    }
    
    public String getEndPoint(String queryParam, String queryValue, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append(QUESTION_MARK);
        sb.append(queryParam);
        sb.append(EQUAL_SIGN);
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
     
    public String getPurpose() {
        return PURPOSE;
    }
    
    public String getDescriptions() {
        return DESCRIPTIONS;
    }
    
    public String getTimestampCreated() {
        return TIME_CREATED;
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
    
    public String getRealmRole() {
        return REALM_ROLE;
    }
    
    public String getTypeRoles() {
        return TYPE_ROLES;
    }
     
    public String getTypeRole() {
        return TYPE_ROLE;
    }
    
    public String getRelationships() {
        return RELATIONSHIPS;
    }
}
