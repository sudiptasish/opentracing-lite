/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.util.OTLConstants;
import com.sc.hm.otl.util.TracingUtility;
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
