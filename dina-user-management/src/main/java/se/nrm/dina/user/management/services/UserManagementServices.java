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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;  
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import se.nrm.dina.user.management.logic.TsvUploader;
import se.nrm.dina.user.management.logic.UserManagement; 
import se.nrm.dina.user.management.utils.CommonString;

/**
 *
 * @author idali
 */
@Path("/user/api/v01") 
@Stateless
public class UserManagementServices implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Inject
    private TsvUploader tsv;
    
    @Inject
    private UserManagement userManagement;
    
    @GET
    @Produces("text/plain")
    public Response doGet() {
        logger.info("doGet");
        return Response.ok("Hello from WildFly Swarm!").build();
    }
    
    @GET 
    @Path("/users")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN}) 
    public Response getUsers(@Context HttpServletRequest req) {
        logger.info("getUsers");
        
        getInfoFromToken(req);
        return Response.ok(userManagement.getUsers(CommonString.getInstance().getDinaRealm())).build();
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
    @Path("/user")
    public Response createUser(String json) {
        
        userManagement.createUser(json);
        return Response.ok().build();
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
}
