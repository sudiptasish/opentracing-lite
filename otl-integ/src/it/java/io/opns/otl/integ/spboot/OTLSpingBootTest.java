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
package io.opns.otl.integ.spboot;

import io.opns.otl.integ.common.AbstractITBase;
import io.opns.otl.integ.common.HttpUtil;
import io.opns.otl.integ.common.ProcessConfig;
import io.opns.otl.integ.common.ProcessUtil;
import io.opns.otl.integ.common.Serializer;
import io.opns.otl.integ.model.Department;
import io.opns.otl.integ.model.Employee;
import io.opns.otl.integ.model.ErrorMessage;
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
public class OTLSpingBootTest extends AbstractITBase {
    
    private static final Logger logger = LoggerFactory.getLogger(OTLSpingBootTest.class);
    
    @BeforeAll
    public static void setupEnv() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Setting up environment for SpringBoot");
            }
            
            ProcessConfig config1 = new ProcessConfig();
            config1.setAppId("ID_1");
            config1.setAppName("EmpSpringBoot");
            config1.setJavaOpts("-Dserver.port=8081");
            config1.setLogFile("target/spring_boot.log");
            config1.setMainClass("io.opns.otl.integ.spboot.EmpSpringBootAppMain");
            
            ProcessConfig config2 = new ProcessConfig();
            config2.setAppId("ID_2");
            config2.setAppName("DeptSpringBoot");
            config2.setJavaOpts("-Dserver.port=8082");
            config2.setLogFile("target/spring_boot.log");
            config2.setMainClass("io.opns.otl.integ.spboot.DeptSpringBootAppMain");
            
            ProcessUtil.start(config1, config2);
            
            if (logger.isInfoEnabled()) {
                logger.info("SpringBoot processes started successfully !");
            }
        }
        catch (RuntimeException e) {
            logger.error("Error in SpringBoot environment setup", e);
            fail(e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Tear down  environment for SpringBoot");
            }
            ProcessUtil.destroy();
        }
        catch (RuntimeException e) {
            logger.error("Error stopping SpringBoot process", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    public void testCreateEmployee() {
        logStart();
        String url = "http://localhost:8081/ecp/api/v1/employees";
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        
        Employee emp = new Employee("10001", "John Cena", "NY", new Date());
        emp.setDept(new Department("WWE", "World Wrestling Entertainment"));
        
        Map<String, String> headers = new HashMap<>();
        
        int code = HttpUtil.rpc(url, "POST", emp, out, headers);
        if (logger.isInfoEnabled()) {
            logger.info("Created employee: " + emp);
        }
        assertEquals(201, code, "Status must be 201");
        
        logEnd();
    }
    
    @Test
    @Order(2)
    public void testGetInvalidEmployee() throws IOException {
        logStart();
        String url = "http://localhost:8081/ecp/api/v1/employees/1018276";
        Map<String, String> headers = new HashMap<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream(128);
        
        int code = HttpUtil.rpc(url, "GET", null, out, headers);
        ErrorMessage errorMsg = Serializer.deserialize(out.toByteArray(), ErrorMessage.class);
        logger.error("Received error from end point: {}", errorMsg);
        
        assertEquals(404, code, "Employee for id 1018276 should not be found");
        
        logEnd();
    }
    
    @Test
    @Order(3)
    public void testGetValidEmployee() throws IOException {
        logStart();
        String url = "http://localhost:8081/ecp/api/v1/employees/10001";
        Map<String, String> headers = new HashMap<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream(128);
        
        int code = HttpUtil.rpc(url, "GET", null, out, headers);
        Employee emp = Serializer.deserialize(out.toByteArray(), Employee.class);
        
        if (logger.isInfoEnabled()) {
            logger.info("Retrieved employee: " + emp);
        }
        assertEquals(200, code, "Employee for id 10001 must exist");
        assertEquals("10001", emp.getId());
        assertEquals("John Cena", emp.getName());
        assertEquals("NY", emp.getLocation());
        
        logEnd();
    }
    
    @Test
    @Order(4)
    public void testCreateEmpAndDept() throws IOException {
        logStart();
        String url = "http://localhost:8081/ecp/api/v1/employees?createDept=true";
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        
        Employee emp = new Employee("10002", "Socretes", "Greece", new Date());
        emp.setDept(new Department("PH_01", "Physics"));
        
        Map<String, String> headers = new HashMap<>();
        
        int code = HttpUtil.rpc(url, "POST", emp, out, headers);
        if (logger.isInfoEnabled()) {
            logger.info("Created employee and department");
        }
        assertEquals(201, code, "Status must be 201");
        
        logEnd();
    }
    
    @Test
    @Order(5)
    public void testGetEmpAndDept() throws IOException {
        logStart();
        String url = "http://localhost:8081/ecp/api/v1/employees/10002?showDept=true";
        ByteArrayOutputStream out = new ByteArrayOutputStream(128);
        
        Map<String, String> headers = new HashMap<>();
        
        int code = HttpUtil.rpc(url, "GET", null, out, headers);
        if (logger.isInfoEnabled()) {
            logger.info("Fetched employee and department");
        }
        assertEquals(200, code, "Status must be 200");
        
        Employee emp = Serializer.deserialize(out.toByteArray(), Employee.class);
        assertEquals("10002", emp.getId());
        assertEquals("Socretes", emp.getName());
        assertEquals("Greece", emp.getLocation());
        
        assertEquals("PH_01", emp.getDept().getId());
        assertEquals("Physics", emp.getDept().getName());
        
        logEnd();
    }
}
