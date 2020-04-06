/*
 *     Copyright 2020 Opentracing-LiTE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sc.hm.otl.integ.jaxrs;

import com.sc.hm.otl.integ.model.Department;
import com.sc.hm.otl.integ.model.Employee;
import com.sc.hm.otl.integ.spboot.ErrorMessage;
import com.sc.hm.otl.integ.spboot.emp.EmployeeService;
import com.sc.hm.otl.util.OTLConstants;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeRest {
    
    private final Logger logger = LoggerFactory.getLogger(EmployeeRest.class);
    
    private static final String DEPT_URL = OTLJaxRsTest.DEPT_SRVC;
    private final EmployeeService service = new EmployeeService();
    
    @POST
    public Response createEmployee(@QueryParam("createDept") @DefaultValue("true") Boolean createDept, Employee emp) {
        service.insert(emp);
        
        if (logger.isInfoEnabled()) {
            logger.info("Employee {} created successfully !", emp);
        }
        if (createDept) {
            Client deptClient = RestClient.restClient();
            
            Response response = deptClient.target(DEPT_URL)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(OTLConstants.BAGGAGE_PREFIX_HEADER + "CorrelationId", "Sudiptasish")
                .post(Entity.entity(emp.getDept(), MediaType.APPLICATION_JSON));
            
            if (logger.isInfoEnabled()) {
                logger.info("Created department: {} through department service. Status: {}"
                    , emp.getDept()
                    , response.getStatus());
            }
        }
        return Response.status(Response.Status.CREATED).build(); 
    }
    
    @GET
    @Path("{empId}")
    public Response getEmployee(@QueryParam("showDept") @DefaultValue("true") Boolean showDept
        , @PathParam("empId") String empId) {
        
        Employee emp = service.select(empId);
        
        if (emp != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Found Employee details for Id: {}. showDept: {}"
                        , emp
                        , showDept);
            }
            if (showDept) {
                Client deptClient = RestClient.restClient();
            
                Response response = deptClient.target(DEPT_URL)
                    .path(emp.getDeptId())
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .get();
                
                Department dept = response.readEntity(Department.class);
                emp.setDept(dept);
                
                if (logger.isInfoEnabled()) {
                    logger.info("Fetched department: {} from department service. Status: {}"
                        , emp.getDept()
                        , response.getStatus());
                }
            }
            return Response.status(Response.Status.OK).entity(emp).build();
        }
        ErrorMessage errorMsg = new ErrorMessage("ERR::NOT::FOUND"
                , "No Employee information found for id: " + emp);
        
        logger.warn("No Employee information found for id: " + empId);
        return Response.status(Response.Status.NOT_FOUND).entity(errorMsg).build();
    }
}
