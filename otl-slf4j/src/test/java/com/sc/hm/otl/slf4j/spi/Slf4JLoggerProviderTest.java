/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.slf4j.spi;

import com.sc.hm.otl.core.log.LoggerAdapter;
import com.sc.hm.otl.core.log.OTLContextLogger;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Slf4JLoggerProviderTest {
    
    @Test
    public void testCreate() {
        try {
            Class.forName("com.sc.hm.otl.core.log.OTLContextLogger");
            
            Field field = OTLContextLogger.class.getDeclaredField("LOGGER_ADAPTER");
            field.setAccessible(true);
            LoggerAdapter adapter = (LoggerAdapter)field.get(null);
            assertTrue(adapter instanceof Slf4JLoggerAdapter, "LoggerAdapter must be of type Slf4JLoggerAdapter");
        }
        catch (ClassNotFoundException e) {
            fail("OTLContextLogger must be found");
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            fail("OTLContextLogger's adapter field must be accessible");
        }
    }
}
