/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.kafka;

import com.sc.hm.otl.integ.common.AbstractITBase;
import com.sc.hm.otl.integ.common.HttpUtil;
import com.sc.hm.otl.integ.common.ProcessConfig;
import com.sc.hm.otl.integ.common.ProcessUtil;
import static com.sc.hm.otl.integ.jaxrs.OTLJaxRsTest.EMP_SRVC;
import com.sc.hm.otl.integ.model.Department;
import com.sc.hm.otl.integ.model.Employee;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class OTLKafkaTest extends AbstractITBase {
    
    private static final Logger logger = LoggerFactory.getLogger(OTLKafkaTest.class);
    
    @BeforeAll
    public static void setupEnv() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Setting up environment for Kafka Broker");
            }
            
            
            
            if (logger.isInfoEnabled()) {
                logger.info("Kafka Broker process started successfully !");
            }
        }
        catch (RuntimeException e) {
            logger.error("Error in Kafka Broker environment setup", e);
            fail(e.getMessage());
        }
    }
    
    //@Test
    @Order(1)
    public void testProduce() {
        logStart();
        
        
        
        logEnd();
    }
}
