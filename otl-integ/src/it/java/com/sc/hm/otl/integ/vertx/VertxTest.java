/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.vertx;

import com.sc.hm.otl.integ.common.AbstractITBase;
import com.sc.hm.otl.integ.common.HttpUtil;
import com.sc.hm.otl.integ.common.ProcessConfig;
import com.sc.hm.otl.integ.common.ProcessUtil;
import com.sc.hm.otl.integ.model.Department;
import com.sc.hm.otl.integ.model.Employee;
import java.io.ByteArrayOutputStream;
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
public class VertxTest extends AbstractITBase {
    
    private static final Logger logger = LoggerFactory.getLogger(VertxTest.class);
    
    public static final String EMP_SRVC = "http://localhost:8080/emp/api/v1/employees";
    public static final String DEPT_SRVC = "http://localhost:8081/dep/api/v1/departments";
    
    @BeforeAll
    public static void setupEnv() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Setting up environment for Vertx");
            }
            
            ProcessConfig config1 = new ProcessConfig();
            config1.setAppId("ID_1");
            config1.setAppName("EmpVerticle");
            config1.setArgs("--port", "8080", "--ctx", "/emp/api/v1");
            config1.setJavaOpts("-Dvertx-config-path=vertx-app.json -Dapp-name=EMP");
            config1.setLogFile("target/vertx.log");
            config1.setDebugEnabled(true);
            config1.setDebugPort(9998);
            config1.setMainClass("com.sc.hm.otl.integ.vertx.VertxMain");
            
            ProcessConfig config2 = new ProcessConfig();
            config2.setAppId("ID_2");
            config2.setAppName("DeptVerticle");
            config2.setJavaOpts("-Dvertx-config-path=vertx-app.json -Dapp-name=DEPT");
            config2.setArgs("--port", "8081", "--ctx", "/dept/api/v1");
            config2.setLogFile("target/vertx.log");
            config2.setDebugEnabled(true);
            config2.setDebugPort(9999);
            config2.setMainClass("com.sc.hm.otl.integ.vertx.VertxMain");
            
            ProcessUtil.start(config1, config2);
            
            if (logger.isInfoEnabled()) {
                logger.info("Vertx processes started successfully !");
            }
        }
        catch (RuntimeException e) {
            logger.error("Error in Vertx environment setup", e);
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

    @AfterAll
    public static void tearDown() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Tear down  environment for JaxRs");
            }
            ProcessUtil.destroy();
        }
        catch (RuntimeException e) {
            logger.error("Error stopping Vertx process", e);
            fail(e.getMessage());
        }
    }
}
