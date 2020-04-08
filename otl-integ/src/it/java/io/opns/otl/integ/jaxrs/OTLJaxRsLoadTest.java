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
package io.opns.otl.integ.jaxrs;

import io.opns.otl.integ.common.AbstractITBase;
import io.opns.otl.integ.common.HttpUtil;
import io.opns.otl.integ.common.ProcessConfig;
import io.opns.otl.integ.common.ProcessUtil;
import io.opns.otl.integ.model.Department;
import io.opns.otl.integ.model.Employee;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLJaxRsLoadTest extends AbstractITBase {
    
    private static final Logger logger = LoggerFactory.getLogger(OTLJaxRsTest.class);
    
    public static final String EMP_SRVC = "http://localhost:7001/empMgmt/api/v1/employees";
    public static final String DEPT_SRVC = "http://localhost:7002/deptMgmt/api/v1/departments";
    
    //@BeforeAll
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
            config1.setMainClass("io.opns.otl.integ.jaxrs.JettyServer");
            
            ProcessConfig config2 = new ProcessConfig();
            config2.setAppId("ID_2");
            config2.setAppName("DeptJettyService");
            config2.setArgs("--port", "7002", "--ctx", "/deptMgmt/api/v1");
            config2.setLogFile("target/jetty.log");
            config2.setMainClass("io.opns.otl.integ.jaxrs.JettyServer");
            
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
    
    //@Test
    @Order(1)
    public void testCreateEmployeesInBulk() throws InterruptedException {
        logStart();
        
        int count = 1000;
        Thread[] t = new Thread[4];
        for (int i = 0; i < t.length; i ++) {
            t[i] = new Thread(new Worker(i * count, count));
        }
        for (int i = 0; i < t.length; i ++) {
            t[i].start();
        }
        for (int i = 0; i < t.length; i ++) {
            t[i].join();
        }
        logEnd();
    }
    
    private static class Worker implements Runnable {
        
        private int startId = 0;
        private int total = 0;
        
        Worker(int startId, int total) {
            this.startId = startId;
            this.total = total;
        }

        @Override
        public void run() {
            int end = startId + total;
            
            try {
                for (int i = startId; i < end; i ++) {
                    String url = EMP_SRVC + "?createDept=true";
                    ByteArrayOutputStream out = new ByteArrayOutputStream(64);

                    Employee emp = new Employee(String.valueOf(i), "Name-" + i, "NY", new Date());
                    emp.setDept(new Department("D-" + i, "World Wrestling Entertainment"));

                    Map<String, String> headers = new HashMap<>();
                    //headers.put(OTLConstants.BAGGAGE_PREFIX_HEADER + "correlationId", "sudip");

                    int code = HttpUtil.rpc(url, "POST", emp, out, headers);
                    if (code != 201) {
                        logger.error("Error occured creating employee. Code: " + code
                            + ". Msg: " + new String(out.toByteArray()));
                        break;
                    }
                    else {
                        if (i % 100 == 0) {
                            if (logger.isInfoEnabled()) {
                                logger.info("Thread " + Thread.currentThread() + " created " + i + " employees");
                            }
                            Thread.sleep(5000);
                        }
                    }
                }
                if (logger.isInfoEnabled()) {
                    logger.info("Thread " + Thread.currentThread() + " finished processing");
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //@AfterAll
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
