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
package io.opns.otl.integ.kafka;

import io.opns.otl.integ.common.AbstractITBase;
import io.opns.otl.integ.common.HttpUtil;
import io.opns.otl.integ.common.ProcessConfig;
import io.opns.otl.integ.common.ProcessUtil;
import static io.opns.otl.integ.jaxrs.OTLJaxRsTest.EMP_SRVC;
import io.opns.otl.integ.model.Department;
import io.opns.otl.integ.model.Employee;
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
