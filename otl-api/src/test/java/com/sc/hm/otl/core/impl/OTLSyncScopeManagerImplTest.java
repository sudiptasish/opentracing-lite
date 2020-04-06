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

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.ctx.OTLContext;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLSyncScopeManagerImplTest {
    
    @Test
    public void testActiveSpan() {
        ScopeManager scopeManager = new OTLSyncScopeManagerImpl();
        Span span = scopeManager.activeSpan();
        
        assertNull(span, "No active span should exist. Span must be null");
    }
    
    @Test
    public void testActivateSpan() {
        ScopeManager scopeManager = new OTLSyncScopeManagerImpl();
        Span span = scopeManager.activeSpan();
        
        assertNull(span, "No active span should exist. Span must be null");
        
        span = new OTLSpanImpl(new OTLTracerImpl()
            , "testActivateSpan"
            , null
            , null
            , null
            , false
            , System.nanoTime() / 1000);
        
        scopeManager.activate(span);
        
        span = scopeManager.activeSpan();
        assertNotNull(span, "Active span must be present");
    }
    
    @Test
    public void testActivateMultipleSpan() {
        OTLTracer tracer = new OTLTracerImpl();
        ScopeManager scopeManager = tracer.scopeManager();
        
        int total = 5;
        OTLSpan[] spans = new OTLSpan[total];
        Scope[] scopes = new Scope[total];
        OTLSpan span = null;
        
        for (int i = 0; i < total; i ++) {
            /*spans[i] = new OTLSpanImpl(tracer
                , "testActivateMultipleSpan_" + i
                , null
                , null
                , false
                , System.nanoTime() / 1000);*/
            
            spans[i] = (OTLSpan)tracer.buildSpan("testActivateMultipleSpan_" + i).start();

            scopes[i] = scopeManager.activate(spans[i]);
        }
        for (int i = total - 1; i > 0; i --) {
            assertEquals(spans[i].context().toTraceId(), spans[i - 1].context().toTraceId(), "TraceId must be same");
            assertTrue(!spans[i].context().toSpanId().equals(spans[i - 1].context().toSpanId()), "SpanId(s) must be unique");
            assertEquals(spans[i].parentSpanId(), spans[i - 1].context().toSpanId());
        }
        assertEquals("", spans[0].parentSpanId());
        
        String traceId;
        String spanId;
        String ops;
        String parentSpanId;
        for (int i = scopes.length - 1; i >= 0; i --) {
            span = (OTLSpan)scopeManager.activeSpan();
            assertEquals("testActivateMultipleSpan_" + i, span.operation());
            
            traceId = OTLContext.get("trc");
            spanId = OTLContext.get("spn");
            ops = OTLContext.get("ops");
            parentSpanId = OTLContext.get("pspn");
            
            assertEquals(spans[i].context().toTraceId(), traceId);
            assertEquals(spans[i].context().toSpanId(), spanId);
            assertEquals(spans[i].operation(), ops);
            assertEquals(spans[i].parentSpanId(), parentSpanId);
            
            scopes[i].close();
        }
        span = (OTLSpan)scopeManager.activeSpan();
        assertTrue(span == null, "No Span should be present");
    }
}
