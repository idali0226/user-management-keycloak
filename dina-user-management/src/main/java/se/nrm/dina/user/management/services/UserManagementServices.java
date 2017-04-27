/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.services;

import java.io.Serializable;  
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;  
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import se.nrm.dina.user.management.logic.TsvUploader;
//import se.nrm.dina.user.management.logic.TsvUploader;

/**
 *
 * @author idali
 */
@Path("/user/api/v01")
//@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
//@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN}) 
@Stateless
public class UserManagementServices implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Inject
    private TsvUploader tsv;
    
    @GET
    @Produces("text/plain")
    public Response doGet() {
        logger.info("doGet");
        return Response.ok("Hello from WildFly Swarm!").build();
    }
    
    @POST
    @Path("/tsv") 
    public Response tsvUpload(String tsvFile) {
        
        logger.info("tsvUpload : {}", tsvFile);
 
        tsv.upload(tsvFile);
        return Response.ok("Tsv upload").build();
    }
}
