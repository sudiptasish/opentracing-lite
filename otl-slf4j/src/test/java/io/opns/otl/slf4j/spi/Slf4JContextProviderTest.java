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
package io.opns.otl.slf4j.spi;

import io.opns.otl.slf4j.spi.Slf4JContextAdapter;
import io.opns.otl.core.ctx.ContextAdapter;
import io.opns.otl.core.ctx.OTLContext;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Slf4JContextProviderTest {
    
    @Test
    public void testCreate() {
        try {
            Class.forName("io.opns.otl.core.ctx.OTLContext");
            
            Field field = OTLContext.class.getDeclaredField("CTX_ADAPTER");
            field.setAccessible(true);
            ContextAdapter adapter = (ContextAdapter)field.get(null);
            assertTrue(adapter instanceof Slf4JContextAdapter, "ContextAdapter must be of type Slf4JContextAdapter");
        }
        catch (ClassNotFoundException e) {
            fail("OTLContext must be found");
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            fail("OTLContext's adapter field must be accessible");
        }
    }
}
