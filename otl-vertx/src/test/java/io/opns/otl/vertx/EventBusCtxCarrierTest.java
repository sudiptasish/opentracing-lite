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
package io.opns.otl.vertx;

import io.opns.otl.vertx.EventBusCtxCarrier;
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
