/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.services;

import java.io.Serializable; 
import javax.ejb.Stateless;
import javax.inject.Inject; 
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam; 
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.logic.UserManagement;

/**
 *
 * @author idali
 */
@Path("/user/api/v01")
@Stateless
public class UserManagementUnsecureService implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private UserManagement userManagement;
    
    @GET
    @Produces("text/plain")
    public Response doGet() {
        logger.info("doGet");
        return Response.ok("Hello from WildFly Swarm!").build();
    }
     

    @GET
    @Path("/users/search")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getUsers(@QueryParam("filter[email]") String email) {
        logger.info("getUsers : email :  {} ", email);
        return Response.ok(userManagement.getUserByUserName(email)).build();    
    }

    @POST
    @Path("/users")
    public Response createUser(String json) { 
        logger.info("createUser : {}", json); 
        return Response.ok(userManagement.createUser(json, false)).build(); 
    }

    @POST
    @Path("/sendemail")    
    public Response sendEmail(@QueryParam("id") String id) {
        
        logger.info("sendEmail : {}", id); 
         
        return Response.ok(userManagement.sendEmail(id, true)).build();
    }
    
    @PUT
    @Path("/recover-password")    
    public Response recoverPassword(@QueryParam("email") String email) {
        
        logger.info("recoverPassword : {} ", email); 
         
        return Response.ok(userManagement.recoverPassword(email)).build();
    }
}
