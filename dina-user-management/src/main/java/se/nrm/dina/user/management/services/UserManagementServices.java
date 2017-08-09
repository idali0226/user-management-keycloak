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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;  
import javax.ws.rs.POST;
import javax.ws.rs.PUT; 
import javax.ws.rs.Path; 
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
import se.nrm.dina.user.management.utils.PATCH;

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
     
  
    @GET    
    @Path("/users/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})     
    public Response getUserById(@Context HttpServletRequest req, @PathParam("id") String id) {
        logger.info("getUserById : id :  {}", id); 
        
        return Response.ok(userManagement.getUserById(id)).build();
    }
     

      
    @GET    
    @Path("/users/search")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response searchUsers(@Context HttpServletRequest req, @Context UriInfo info) {
        logger.info("searchUsers");

        MultivaluedMap<String, String> map = info.getQueryParameters();
        String status = map.getFirst("filter[status]");
        String username = map.getFirst("filter[email]");
 
        if (username != null) {
            return Response.ok(userManagement.getUserByUserName(username)).build();
        } else if (status != null && !status.isEmpty()) {
            StringBuilder sb = new StringBuilder(status);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            status = sb.toString(); 
            return Response.ok(userManagement.getUserByAccountStatus(status)).build();
        } else {
            return Response.ok(userManagement.getUsers()).build();
        }

//        if(isAdminRole(req)) {
//            MultivaluedMap<String, String> map = info.getQueryParameters();
//            String status = map.getFirst("filter[status]");
//            String username = map.getFirst("filter[email]");
//
//            logger.info("status : {}", status);
//            if(username != null) {
//                return Response.ok(userManagement.getUserByUserName(username)).build();
//            } else if(status != null && !status.isEmpty()) {
//                StringBuilder sb=new StringBuilder(status);
//                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
//                status = sb.toString();
//
//                return Response.ok(userManagement.getUserByAccountStatus(status)).build();
//            } else {
//                return Response.ok(userManagement.getUsers()).build();
//            } 
//        }
//        
//        return Response.status(Response.Status.UNAUTHORIZED).build();
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
    public Response getRealmByName(@Context HttpServletRequest req, @QueryParam("realm") String realm) {
        logger.info("getRealmsByName: {}", realm); 
         
        return Response.ok(realmManagement.getRealmByRealmName(realm)).build();
    }
    
    @GET    
    @Path("/realms/search")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})     
    public Response getRealmsByName(@Context HttpServletRequest req, @QueryParam("filter[realm]") String realm) {
        logger.info("getRealmsByName: {}", realm); 
         
        return Response.ok(realmManagement.getRealmByRealmName(realm)).build();
    }
    
    
    private boolean isAdminRole(HttpServletRequest req) { 
        Principal userPrincipal = req.getUserPrincipal();        
        if (userPrincipal instanceof KeycloakPrincipal) {   
            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) userPrincipal;
            AccessToken token = kp.getKeycloakSecurityContext().getToken();        
            Map<String, Access> accessMap = token.getResourceAccess();
            Access access = accessMap.get("user-management");
            return access.getRoles().toString().equals("[admin]"); 
        }
        return false;
    }
      
    @POST
    @Path("/users")
    public Response createUser(@Context HttpServletRequest req, String json) {
        logger.info("createUser : {}", json);
          
        if(isAdminRole(req)) {
            return Response.ok(userManagement.createUser(json, true)).build();
        } 
        return Response.status(Response.Status.UNAUTHORIZED).build();
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
    public Response sendEmail(@Context HttpServletRequest req, @QueryParam("id") String id, @QueryParam("isPendingUser") boolean isPendingUser) { 
        logger.info("sendEmail : {} -- {}", id, isPendingUser); 
        
        if(isAdminRole(req)) {
            return Response.ok(userManagement.sendEmail(id, isPendingUser)).build(); 
        } 
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    
    
    
    @PUT
    @Path("/enableDisableUser")
    public Response userActions(@Context HttpServletRequest req, @QueryParam("id") String id, @QueryParam("action") String action) {
        logger.info("userActions : {} -- {}", id, action); 
        
        if(isAdminRole(req)) {
            return Response.ok(action.equals("enableUser") ? userManagement.enableUser(id) : userManagement.disableUser(id)).build();
        } 
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    
    @PATCH
    @Path("/users/{id}")
    public Response updateUser(@Context HttpServletRequest req, String json, @PathParam("id") String id) {
        logger.info("updateUser : {}  --  {}", json, id);
        return Response.ok(userManagement.updateUser(json)).build();
    }
    
    @PUT
    @Path("/logout")
    public Response logout(@QueryParam("id") String id) {
        logger.info("logout : {}", id);  
        return Response.ok(userManagement.logout(id)).build();
    } 
    
    @DELETE
    @Path("/users/{id}") 
    public Response delete(@Context HttpServletRequest req, @PathParam("id") String id) {
        logger.info("delete : {}", id);
        
         if(isAdminRole(req)) {
            userManagement.deleteUser(id);
            return Response.noContent().build();
        } 
        return Response.status(Response.Status.UNAUTHORIZED).build();  
    }
    
    
    
    
    
    //    @GET    
//    @Path("/users")
//    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
//    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})     
//    public Response getUsers(   @Context HttpServletRequest req, 
//                                @QueryParam("filter[email]") String email, 
//                                @QueryParam("filter[status]") String status, 
//                                @QueryParam("filter[login]") boolean isLogin,
//                                @Context UriInfo info) {
//        logger.info("getUsers : email :  {} -- {}", email, isLogin + " " + status);  
//        
//        MultivaluedMap<String, String> map = info.getQueryParameters();
//        
//        logger.info("Map : {}", map);
//        map.entrySet().stream()
//                .forEach(m -> {
//                    logger.info("key : {}", m.getKey());
//                    logger.info("value: {}", m.getValue());
//                });
//        
// 
//        if(isLogin) {
//            return Response.ok(userManagement.getLoggedInUser()).build();                   // Returns a list of logged in users
//        } else {
//            if(email == null) { 
//                return Response.ok(userManagement.getUsers()).build();
//            } else { 
//                return Response.ok(userManagement.getUserByUserName(email)).build();
//            } 
//        }
//    }
    
    
    //    private void getInfoFromToken(HttpServletRequest req) {        
//        logger.info("getInfoFromToken");
//        
//        Principal userPrincipal = req.getUserPrincipal();        
//        if (userPrincipal instanceof KeycloakPrincipal) {            
//            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) userPrincipal;
//            
//            AccessToken token = kp.getKeycloakSecurityContext().getToken();
//            logger.info("name and id: {} -- {}", token.getName(), token.getId());            
//            Map<String, Access> accessMap = token.getResourceAccess();
//            accessMap.entrySet().stream()
//                    .forEach(a -> {
//                        logger.info("access : {} --- {}", a.getKey(), a.getValue().getRoles().toString());
//                    });
//            
//            Map<String, Object> map = kp.getKeycloakSecurityContext().getToken().getOtherClaims();
//            logger.info("map : {}", map);
//            
//            map.entrySet().stream()
//                    .forEach(e -> {
//                        logger.info("value : {} -- {}", e.getKey(), e.getValue());
//                    });            
//        } else {
//            logger.info("nothing found");
//        } 
//    }
    
    
    
    
    
        
    
//    @PUT
//    @Path("/enableUser")    
//    public Response enableUser(@QueryParam("id") String id) {
//        
//        logger.info("enableUser : {}", id ); 
//        return Response.ok(userManagement.enableUser(id)).build();
//    }
//    
//        
//    @PUT
//    @Path("/disableUser")    
//    public Response disableUser(@QueryParam("id") String id) { 
//        logger.info("disableUser : {}", id); 
//        return Response.ok(userManagement.disableUser(id)).build();
//    }
    
}
