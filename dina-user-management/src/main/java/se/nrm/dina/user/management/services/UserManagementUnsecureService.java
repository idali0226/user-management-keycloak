/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.services;

import java.io.Serializable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.user.management.logic.UserManagement;
import se.nrm.dina.user.management.utils.CommonString;

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
    @Path("/users")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})     
    public Response getUsers(@Context HttpServletRequest req, @QueryParam("filter[email]") String email, @QueryParam("filter[login]") boolean isLogin) {
        logger.info("getUsers : email :  {} - login: {}", email, isLogin); 
        
        if(isLogin) {
            return Response.ok(userManagement.getLoggedInUser()).build();
        } else {
            if(email == null) {  
                return Response.ok(userManagement.getUsers(CommonString.getInstance().getDinaRealm(), null)).build();
            } else { 
                return Response.ok(userManagement.getUsers(CommonString.getInstance().getDinaRealm(), email)).build();
            }
        } 
    }
    
 
    
    @POST
    @Path("/users")
    public Response createUser(String json) {
        logger.info("createUser : {}", json);
         
        return Response.ok(userManagement.createUser(json)).build();
    }
    
    @POST
    @Path("/sendemail")    
    public Response sendEmail(@QueryParam("id") String id) {
        
        logger.info("sendEmail : {}", id); 
        return Response.ok(userManagement.sendVerificationEmail(id)).build();
    }
}
