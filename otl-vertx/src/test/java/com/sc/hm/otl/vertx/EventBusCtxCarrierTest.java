/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.vertx.core.MultiMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Sudiptasish Chanda
 */
public class EventBusCtxCarrierTest {
    
    @Test
    public void testIterator() {
        MultiMap map = MultiMap.caseInsensitiveMultiMap();
        map.add("app.name", "test.app");
        map.add("app.env", "test.env");
        
        EventBusCtxCarrier carrier = new EventBusCtxCarrier(map);
        int count  = 0;
        
        for (Iterator<Map.Entry<String, String>> itr = carrier.iterator(); itr.hasNext(); ) {
            Map.Entry<String, String> me = itr.next();
            if ("app.name".equals(me.getKey())) {
                assertEquals("test.app", me.getValue());
                count ++;
            }
            else if ("app.env".equals(me.getKey())) {
                assertEquals("test.env", me.getValue());
                count ++;
            }
        }
        assertEquals(2, count, "Element in event bus carrier must be 2");
    }
    
}
