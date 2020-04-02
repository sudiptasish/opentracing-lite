/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.spboot.emp;

import com.sc.hm.otl.integ.model.Department;
import com.sc.hm.otl.integ.model.Employee;
import com.sc.hm.otl.integ.spboot.ErrorMessage;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Sudiptasish Chanda
 */
@RestController
@RequestMapping(path = "/ecp/api/v1/employees"
    , produces = MediaType.APPLICATION_JSON_VALUE
    , consumes = MediaType.APPLICATION_JSON_VALUE)
public class EmpRestController {
    
    private final Logger logger = LoggerFactory.getLogger(EmpRestController.class);
    
    private static final String DEPT_URL = "http://localhost:8082/ecp/api/v1/departments";
   
    @Autowired
    private EmployeeService service;
    
    @Autowired
    private RestTemplate template;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createEmployee(@RequestBody Employee emp
            , @RequestParam(name = "createDept", defaultValue = "false") Boolean createDept) {
        
        service.insert(emp);
        if (logger.isInfoEnabled()) {
            logger.info("Employee {} created successfully !", emp);
        }
        if (createDept) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            
            HttpEntity<Department> request = new HttpEntity<>(emp.getDept(), headers);
            ResponseEntity<String> response = template.exchange(DEPT_URL
                , HttpMethod.POST
                , request
                , String.class);
            
            if (logger.isInfoEnabled()) {
                logger.info("Created department: {} through department service. Status: {}"
                    , emp.getDept()
                    , response.getStatusCodeValue());
            }
        }
        return ResponseEntity.created(URI.create("/employees/"  + emp.getId())).build();
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "{empId}")
    public ResponseEntity getEmployee(@PathVariable("empId") String empId
            , @RequestParam(name = "showDept", defaultValue = "false") Boolean showDept) {
        
        Employee emp = service.select(empId);
        
        if (emp != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Found Employee details for Id: {}. showDept: {}"
                        , emp
                        , showDept);
            }
            if (showDept) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                
                HttpEntity<Department> request = new HttpEntity<>(headers);
                ResponseEntity<Department> response = template.exchange(
                    DEPT_URL + "/" + emp.getDeptId()
                    , HttpMethod.GET
                    , request
                    , Department.class);

                emp.setDept(response.getBody());
                if (logger.isInfoEnabled()) {
                    logger.info("Fetched department: {} from department service. Status: {}"
                        , emp.getDept()
                        , response.getStatusCodeValue());
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(emp);
        }
        ErrorMessage errorMsg = new ErrorMessage("ERR::NOT::FOUND"
                , "No Employee information found for id: " + emp);
        
        logger.warn("No Employee information found for id: " + empId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllEmployees() {
        Collection<Employee> employees = service.selectAll();
        
        if (employees != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Found {} employee(s)", employees);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(employees);
    }
}
