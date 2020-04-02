/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TracingUtilityTest {
    
    @Test
    public void testNewTraceId() {
        String traceId = TracingUtility.newTraceId();
        assertEquals(TracingUtility.TRACE_ID_LENGTH, traceId.length()
            , "TraceId must be " + TracingUtility.TRACE_ID_LENGTH + " character length");
    }
    
    @Test
    public void testNewSpanId() {
        String spanId = TracingUtility.newSpanId();
        assertEquals(TracingUtility.SPAN_ID_LENGTH, spanId.length()
            , "SpanId must be " + TracingUtility.SPAN_ID_LENGTH + " character length");
    }
}
