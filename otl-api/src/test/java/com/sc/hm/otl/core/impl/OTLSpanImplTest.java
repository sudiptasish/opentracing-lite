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
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLReference;
import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.util.OTLConstants;
import io.opentracing.References;
import io.opentracing.tag.Tags;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLSpanImplTest {
    
    @Test
    public void testCreateInvalidSpan() {
        Exception e = assertThrows(
            NullPointerException.class,
            () -> new OTLSpanImpl(null
                , "testCreateInvalidSpan"
                , null
                , null
                , null
                , false
                , System.nanoTime() / 1000));
        
        assertTrue(e.getMessage().equals("Tracer should be non-null"));
        
        e = assertThrows(
            NullPointerException.class,
            () -> new OTLSpanImpl(new OTLTracerImpl()
                , null
                , null
                , null
                , null
                , false
                , System.nanoTime() / 1000));
        
        assertTrue(e.getMessage().equals("Operation name should be non-null"));
    }
    
    @Test
    public void testCreateValidSpan() {
        Map<String, String> baggages = new HashMap<>();
        baggages.put(OTLConstants.BAGGAGE_PREFIX_HEADER + "CorrelationId", "C_1");
        
        String operation = "testCreateValidSpan";
        long startTime = System.nanoTime() / 1000;
        
        OTLSpanContext parentCtx = new OTLSpanContextImpl("trc_1", "spn_1", baggages, 0);
        List<OTLReference> references = new ArrayList<>();
        references.add(new OTLReferenceImpl(References.CHILD_OF, parentCtx));
        
        OTLSpan span = new OTLSpanImpl(new OTLTracerImpl()
                , operation
                , references
                , null
                , null
                , false
                , startTime);
        
        assertNotNull(span, "Span should have been created");
        assertEquals("trc_1", span.context().toTraceId());
        assertEquals("spn_1", span.parentSpanId());
        assertEquals(startTime, span.startTime());
        assertNotNull(span.context().toSpanId());
        assertNotEquals("spn_1", span.context().toSpanId());
        assertEquals("C_1", span.getBaggageItem(OTLConstants.BAGGAGE_PREFIX_HEADER + "CorrelationId"));
        
        span.setBaggageItem(OTLConstants.BAGGAGE_PREFIX_HEADER + "TenantId", "opcTenant");
        
        int counter = 0;
        for (Map.Entry<String, String> me : span.context().baggageItems()) {
            if ((OTLConstants.BAGGAGE_PREFIX_HEADER + "CorrelationId").equals(me.getKey())) {
                assertEquals("C_1", me.getValue());
                counter ++;
            }
            else if ((OTLConstants.BAGGAGE_PREFIX_HEADER + "TenantId").equals(me.getKey())) {
                assertEquals("opcTenant", me.getValue());
                counter ++;
            }
        }
        assertEquals(2, counter, "Must be two baggage items");
        
        Map<String, Object> spanTags = ((OTLSpan)span).tags();
        assertEquals(0, spanTags.size(), "Initial set of tags should be empty");
        
        span.setTag("tag_1", "value_1");
        span.setTag("tag_2", "value_2");
        span.setTag(Tags.SPAN_KIND_CLIENT, "rest-client");
        
        spanTags = span.tags();
        assertEquals(3, spanTags.size(), "Must have three tags");

        counter = 0;
        for (Map.Entry<String, Object> me : spanTags.entrySet()) {
            if (me.getKey().equals("tag_1")) {
                assertEquals("value_1", me.getValue());
                counter ++;
            }
            else if (me.getKey().equals("tag_2")) {
                assertEquals("value_2", me.getValue());
                counter ++;
            }
            else if (me.getKey().equals(Tags.SPAN_KIND_CLIENT)) {
                assertEquals("rest-client", me.getValue());
                counter ++;
            }
        }
        assertEquals(3, counter, "Must be three tags");
    }
}
