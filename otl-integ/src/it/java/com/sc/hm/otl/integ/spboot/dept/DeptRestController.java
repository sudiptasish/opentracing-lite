/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.spboot.dept;

import com.sc.hm.otl.integ.model.Department;
import com.sc.hm.otl.integ.spboot.ErrorMessage;
import java.net.URI;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sudiptasish Chanda
 */
@RestController
@RequestMapping(path = "/ecp/api/v1/departments"
    , produces = MediaType.APPLICATION_JSON_VALUE
    , consumes = MediaType.APPLICATION_JSON_VALUE)
public class DeptRestController {
    
    private final Logger logger = LoggerFactory.getLogger(DeptRestController.class);
   
    @Autowired
    private DeptService service;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createDepartment(@RequestBody Department dept) {
        service.insert(dept);
        if (logger.isInfoEnabled()) {
            logger.info("Department {} created successfully !", dept);
        }
        return ResponseEntity.created(URI.create("/departments/"  + dept.getId())).build();
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "{deptId}")
    public ResponseEntity getDepartment(@PathVariable("deptId") String deptId) {
        Department dept = service.select(deptId);
        
        if (dept != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Found Department details for Id: {}.", deptId);
            }
            return ResponseEntity.status(HttpStatus.OK).body(dept);
        }
        ErrorMessage errorMsg = new ErrorMessage("ERR::NOT::FOUND"
                , "No Department information found for id: " + deptId);
        
        logger.warn("No Department information found for id: " + deptId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllDepartments() {
        Collection<Department> depts = service.selectAll();
        
        if (depts != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Found {} department(s)", depts);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(depts);
    }
}
