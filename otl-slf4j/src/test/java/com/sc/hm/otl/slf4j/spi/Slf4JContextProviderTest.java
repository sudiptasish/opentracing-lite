/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.slf4j.spi;

import com.sc.hm.otl.core.ctx.ContextAdapter;
import com.sc.hm.otl.core.ctx.OTLContext;
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
            Class.forName("com.sc.hm.otl.core.ctx.OTLContext");
            
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
