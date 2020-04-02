/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.jaxrs;

import com.sc.hm.otl.integ.model.Department;
import com.sc.hm.otl.integ.spboot.ErrorMessage;
import com.sc.hm.otl.integ.spboot.dept.DeptService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
@Path("/departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepartmentRest {
    
    private final Logger logger = LoggerFactory.getLogger(DepartmentRest.class);
    
    private final DeptService service = new DeptService();
    
    @POST
    public Response createDepartment(Department dept) {
        service.insert(dept);
        if (logger.isInfoEnabled()) {
            logger.info("Department {} created successfully !", dept);
        }
        return Response.status(Response.Status.CREATED).build(); 
    }
    
    @GET
    @Path("{deptId}")
    public Response getDepartment(@PathParam("deptId") String deptId) {
        Department dept = service.select(deptId);
        
        if (dept != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Found Department details for Id: {}.", deptId);
            }
            return Response.status(Response.Status.OK).entity(dept).build();
        }
        ErrorMessage errorMsg = new ErrorMessage("ERR::NOT::FOUND"
                , "No Department information found for id: " + deptId);
        
        logger.warn("No Department information found for id: " + deptId);
        return Response.status(Response.Status.NOT_FOUND).entity(errorMsg).build();
    }
}
