/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.OTLAsyncScope;
import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.OTLSyncScopeManager;
import com.sc.hm.otl.util.OTLConstants;
import io.opentracing.tag.Tags;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TracingVtxHandlerTest extends AbstractUnitTest {
    
    @Test
    public void testSuccesHandle() throws InterruptedException {
        String uri = "/ctx/api/v1/employees";
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        headers.put(OTLConstants.BAGGAGE_ITEMS_HEADER + "CorrelationId", "ABCDEF");
        
        int statusCode = 201;
        boolean failed = false;
        
        CountDownLatch latch = new CountDownLatch(1);
        
        HttpServerRequest request = new MockHttpServerRequest(uri, method, headers);
        HttpServerResponse response = new MockHttpServerResponse(statusCode);

        RoutingContext context = new MockRoutingContext(request, response, failed, true, latch);
        
        TracingVtxHandler vtxHandler = new TracingVtxHandler();
        vtxHandler.handle(context);
        
        latch.await();
        
        OTLSpanVisitor visitor = extract();
        if (visitor instanceof MockSpanVisitor) {
            List<OTLSpan> spans = ((MockSpanVisitor)visitor).getSpans();
            assertEquals(1, spans.size(), "Must have one span created");
            
            OTLSpan span = spans.get(0);
            
            assertNotNull(span.context().toTraceId(), "TraceId must be non null");
            assertNotNull(span.context().toSpanId(), "SpanId must be non null");
            assertEquals(method, span.operation(), "Operation must be " + method);
            assertTrue(span.startTime() > 0, "Span Start time must be greater than 0");
            assertTrue(span.endTime() > span.startTime(), "Span End time must be greater than Start time");
            
            Map<String, Object> tags = span.tags();
            // Total number of tags set inside onRequest & onResponse method is 5.
            assertEquals(5, tags.size()
                , "Total number of tags set by FilterSpanDecorator.onRequest() must be 5");

            // Now start checking individual tags.
            Object val = tags.get(Tags.COMPONENT.getKey());
            assertEquals(MiddlewareSpanDecorator.VERTX_WEB, val.toString()
                , "Value of tag ["
                    + Tags.COMPONENT.getKey()
                    + "] must be "
                    + MiddlewareSpanDecorator.VERTX_WEB);

            val = tags.get(Tags.HTTP_METHOD.getKey());
            assertEquals(method, val.toString()
                , "Value of tag ["
                    + Tags.HTTP_METHOD.getKey()
                    + "] must be "
                    + method);

            val = tags.get(Tags.HTTP_URL.getKey());
            assertEquals(uri, val.toString()
                , "Value of tag ["
                    + Tags.HTTP_URL.getKey()
                    + "] must be "
                    + uri);

            val = tags.get(Tags.SPAN_KIND.getKey());
            assertEquals(Tags.SPAN_KIND_SERVER, val.toString()
                , "Value of tag ["
                    + Tags.SPAN_KIND.getKey()
                    + "] must be "
                    + Tags.SPAN_KIND_SERVER);

            val = tags.get(Tags.HTTP_STATUS.getKey());
            assertEquals(statusCode, Integer.parseInt(val.toString())
                , "Value of tag ["
                    + Tags.HTTP_STATUS.getKey()
                    + "] must be "
                    + statusCode);
            
            spans.clear();
            
            assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
            assertTrue(tracer.activeSpan() == null, "No active span should be present");
            
            OTLAsyncScope scope = (OTLAsyncScope)TracingVtxHandler.scope(context);
            assertTrue(scope == null, "No more span should be present in routing context");
        }
        else {
            Assertions.fail("Span Visitor must be of type MockSpanVisitor");
        }
    }
    
    @Test
    public void testFailureHandle() throws InterruptedException {
        String uri = "/ctx/api/v1/employees";
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        headers.put(OTLConstants.BAGGAGE_ITEMS_HEADER + "CorrelationId", "ABCDEF");
        
        int statusCode = 400;
        boolean failed = true;
        
        CountDownLatch latch = new CountDownLatch(1);
        
        HttpServerRequest request = new MockHttpServerRequest(uri, method, headers);
        HttpServerResponse response = new MockHttpServerResponse(statusCode);

        RoutingContext context = new MockRoutingContext(request, response, failed, true, latch);
        
        TracingVtxHandler vtxHandler = new TracingVtxHandler();
        vtxHandler.handle(context);
        
        OTLSpanVisitor visitor = extract();
        if (visitor instanceof MockSpanVisitor) {
            List<OTLSpan> spans = ((MockSpanVisitor)visitor).getSpans();
            assertEquals(0, spans.size(), "Must not have any span created");
            
            assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
            assertTrue(tracer.activeSpan() == null, "No active span should be present");
            
            OTLAsyncScope scope = (OTLAsyncScope)TracingVtxHandler.scope(context);
            assertTrue(scope == null, "No more span should be present in routing context");
        }
        else {
            Assertions.fail("Span Visitor must be of type MockSpanVisitor");
        }
    }
}
