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
