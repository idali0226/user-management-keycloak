/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.services;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map; 
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path; 
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.logic.ClientManagement;
import se.nrm.dina.user.management.logic.RealmManagement;
import se.nrm.dina.user.management.logic.RoleManagement;
import se.nrm.dina.user.management.logic.TsvUploader;
import se.nrm.dina.user.management.logic.UserManagement;
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
@Path("/user/api/v01/secure")
@Stateless
public class UserManagementServices implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Inject
    private TsvUploader tsv;
    
    @Inject
    private UserManagement userManagement;
    
    @Inject
    private ClientManagement clientManagement;
    
    @Inject
    private RealmManagement realmManagement;
    
    @Inject
    private RoleManagement roleManagement;
    
    @GET
    @Produces("text/plain")
    public Response doGet() {
        logger.info("doGet");
        return Response.ok("Hello from WildFly Swarm!").build();
    }
    
//    @GET    
//    @Path("/users")
//    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
//    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})    
//    public Response getUsers(@Context HttpServletRequest req) {
//        logger.info("getUsers");
//        
//        getInfoFromToken(req);
//        return Response.ok(userManagement.getUsers(CommonString.getInstance().getDinaRealm())).build();
//    }
  
    @GET    
    @Path("/users/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})     
    public Response getUserById(@Context HttpServletRequest req, @PathParam("id") String id) {
        logger.info("getUserById : id :  {}", id); 
        
        return Response.ok(userManagement.getUserById(CommonString.getInstance().getDinaRealm(), id)).build();
    }
     
    @GET    
    @Path("/users")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})     
    public Response getUsers(@Context HttpServletRequest req, @QueryParam("filter[email]") String email) {
        logger.info("getUsers : email :  {}", email); 
        
        if(email == null) {
            getInfoFromToken(req);
            
            realmManagement.getRealmEvent(CommonString.getInstance().getDinaRealm());
            return Response.ok(userManagement.getUsers(CommonString.getInstance().getDinaRealm(), null)).build();
        } else { 
            return Response.ok(userManagement.getUsers(CommonString.getInstance().getDinaRealm(), email)).build();
        } 
    }
      
    @GET    
    @Path("/clients")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})     
    public Response getRealmClients(@Context HttpServletRequest req, @QueryParam("realm") String realm) {
        logger.info("getRealmClients: {}", realm);  
        return Response.ok(clientManagement.getAllTheClients()).build();
    }
    
    @GET    
    @Path("/roles/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})     
    public Response getRolesById(@Context HttpServletRequest req, @PathParam("id") String id) {
        logger.info("getRolesById: {}", id);  
        return Response.ok(roleManagement.getRoleById(id)).build();
    }
    
    @GET    
    @Path("/realms")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})     
    public Response getRealmsByName(@Context HttpServletRequest req, @QueryParam("filter[realm]") String realm) {
        logger.info("getRealmsByName: {}", realm); 
        
        return Response.ok("ok").build();
    }
     
    private void getInfoFromToken(HttpServletRequest req) {        
        logger.info("getInfoFromToken");
        
        Principal userPrincipal = req.getUserPrincipal();        
        if (userPrincipal instanceof KeycloakPrincipal) {            
            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) userPrincipal;
            
            AccessToken token = kp.getKeycloakSecurityContext().getToken();
            logger.info("name and id: {} -- {}", token.getName(), token.getId());            
            Map<String, Access> accessMap = token.getResourceAccess();
            accessMap.entrySet().stream()
                    .forEach(a -> {
                        logger.info("access : {} --- {}", a.getKey(), a.getValue().getRoles().toString());
                    });
            
            Map<String, Object> map = kp.getKeycloakSecurityContext().getToken().getOtherClaims();
            logger.info("map : {}", map);
            
            map.entrySet().stream()
                    .forEach(e -> {
                        logger.info("value : {} -- {}", e.getKey(), e.getValue());
                    });            
        } else {
            logger.info("nothing found");
        } 
    }
 
    @POST
    @Path("/users")
    public Response createUser(String json) {
        logger.info("createUser : {}", json);
         
        return Response.ok(userManagement.createUser(json)).build();
    }
    
    @POST
    @Path("/group")
    public Response addGroup(String json) {
        
        return Response.ok("Tsv upload").build();
    }
    
    @POST
    @Path("/tsv")    
    public Response tsvUpload(String tsvFile) {
        
        logger.info("tsvUpload : {}", tsvFile);
        
        tsv.upload(tsvFile);
        return Response.ok("Tsv upload").build();
    }
    
    
    @POST
    @Path("/sendemail")    
    public Response sendEmail(@QueryParam("id") String id) {
        
        logger.info("sendEmail : {}", id); 
        return Response.ok(userManagement.sendVerificationEmail(id)).build();
    }
    
    @PUT
    @Path("/enableUser")    
    public Response enableUser(@QueryParam("id") String id) {
        
        logger.info("enableUser : {}", id); 
        return Response.ok(userManagement.enableUser(id)).build();
    }
    
    @PUT
    @Path("/updateUser")
    public Response updateUser(String json) {
        logger.info("updateUser : {}", json);
         
        return Response.ok(userManagement.updateUser(json)).build();
    }
}
