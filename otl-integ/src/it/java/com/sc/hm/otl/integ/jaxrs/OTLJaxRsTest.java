/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.jaxrs;

import com.sc.hm.otl.integ.common.AbstractITBase;
import com.sc.hm.otl.integ.common.HttpUtil;
import com.sc.hm.otl.integ.common.ProcessConfig;
import com.sc.hm.otl.integ.common.ProcessUtil;
import com.sc.hm.otl.integ.common.Serializer;
import com.sc.hm.otl.integ.model.Department;
import com.sc.hm.otl.integ.model.Employee;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OTLJaxRsTest extends AbstractITBase {
    
    private static final Logger logger = LoggerFactory.getLogger(OTLJaxRsTest.class);
    
    public static final String EMP_SRVC = "http://localhost:7001/empMgmt/api/v1/employees";
    public static final String DEPT_SRVC = "http://localhost:7002/deptMgmt/api/v1/departments";
    
    @BeforeAll
    public static void setupEnv() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Setting up environment for JaxRs");
            }
            
            ProcessConfig config1 = new ProcessConfig();
            config1.setAppId("ID_1");
            config1.setAppName("EmpJettyService");
            config1.setArgs("--port", "7001", "--ctx", "/empMgmt/api/v1");
            config1.setLogFile("target/jetty.log");
            config1.setDebugEnabled(true);
            config1.setDebugPort(9999);
            config1.setMainClass("com.sc.hm.otl.integ.jaxrs.JettyServer");
            
            ProcessConfig config2 = new ProcessConfig();
            config2.setAppId("ID_2");
            config2.setAppName("DeptJettyService");
            config2.setArgs("--port", "7002", "--ctx", "/deptMgmt/api/v1");
            config2.setLogFile("target/jetty.log");
            config2.setMainClass("com.sc.hm.otl.integ.jaxrs.JettyServer");
            
            ProcessUtil.start(config1, config2);
            
            if (logger.isInfoEnabled()) {
                logger.info("Jetty Jax-rs processes started successfully !");
            }
        }
        catch (RuntimeException e) {
            logger.error("Error in JaxRs environment setup", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    public void testCreateEmployee() {
        logStart();
        String url = EMP_SRVC + "?createDept=true";
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        
        Employee emp = new Employee("10001", "John Cena", "NY", new Date());
        emp.setDept(new Department("WWE", "World Wrestling Entertainment"));
        
        Map<String, String> headers = new HashMap<>();
        //headers.put(OTLConstants.BAGGAGE_PREFIX_HEADER + "correlationId", "sudip");
        
        int code = HttpUtil.rpc(url, "POST", emp, out, headers);
        if (logger.isInfoEnabled()) {
            logger.info("Created employee: " + emp);
        }
        assertEquals(201, code, "Status must be 201");
        
        logEnd();
    }
    
    @Test
    @Order(2)
    public void testGetEmployee() throws IOException {
        logStart();
        String url = EMP_SRVC + "/10001?showDept=true";
        ByteArrayOutputStream out = new ByteArrayOutputStream(128);
        
        Map<String, String> headers = new HashMap<>();
        
        int code = HttpUtil.rpc(url, "GET", null, out, headers);
        if (logger.isInfoEnabled()) {
            logger.info("Fetched employee and department");
        }
        assertEquals(200, code, "Status must be 200");
        
        Employee emp = Serializer.deserialize(out.toByteArray(), Employee.class);
        assertEquals("10001", emp.getId());
        assertEquals("John Cena", emp.getName());
        assertEquals("NY", emp.getLocation());
        
        assertEquals("WWE", emp.getDept().getId());
        assertEquals("World Wrestling Entertainment", emp.getDept().getName());
        
        logEnd();
    }

    @AfterAll
    public static void tearDown() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Tear down  environment for JaxRs");
            }
            ProcessUtil.destroy();
        }
        catch (RuntimeException e) {
            logger.error("Error stopping JaxRs process", e);
            fail(e.getMessage());
        }
    }
    
}
