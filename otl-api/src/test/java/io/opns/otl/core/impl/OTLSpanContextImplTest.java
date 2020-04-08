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
package io.opns.otl.core.impl;

import io.opns.otl.core.OTLSpanContext;
import io.opns.otl.util.OTLConstants;
import io.opns.otl.util.TracingUtility;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLSpanContextImplTest {
    
    @Test
    public void testCreateValidContext() {
        String traceId = TracingUtility.newTraceId();
        String spanId = TracingUtility.newSpanId();
        Map<String, String> baggageItems = new HashMap<>();
        baggageItems.put(OTLConstants.BAGGAGE_PREFIX_HEADER + "CorrelationId", "C_1234");
        
        OTLSpanContext context = new OTLSpanContextImpl(traceId, spanId, baggageItems, 0);
        
        assertEquals(traceId, context.toTraceId());
        assertEquals(spanId, context.toSpanId());
        assertEquals(baggageItems, context.getBaggageItems());
    }
    
    @Test
    public void testCreateContextWithInvalidTraceId() {
        String traceId = null;
        String spanId = TracingUtility.newSpanId();
        Map<String, String> baggageItems = new HashMap<>();
        
        Exception e = assertThrows(
            NullPointerException.class,
            () -> new OTLSpanContextImpl(traceId, spanId, baggageItems, 0));
        
        assertTrue(e.getMessage().equals("TraceId must be non-null"));
    }
    
    @Test
    public void testCreateContextWithInvalidSpanId() {
        String traceId = TracingUtility.newTraceId();
        String spanId = null;
        Map<String, String> baggageItems = new HashMap<>();
        
        Exception e = assertThrows(
            NullPointerException.class,
            () -> new OTLSpanContextImpl(traceId, spanId, baggageItems, 0));
        
        assertTrue(e.getMessage().equals("SpanId must be non-null"));
    }
}
