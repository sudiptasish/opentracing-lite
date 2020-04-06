/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.OTLSyncScopeManager;
import static com.sc.hm.otl.jaxrs.filter.AbstractUnitTest.tracer;
import com.sc.hm.otl.util.OTLConstants;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TracingClientFilterTest extends AbstractUnitTest {
    
    @Test
    public void testRequestNoSpan() throws URISyntaxException, MalformedURLException, IOException {
        System.setProperty("jax.rs.span", "false");
        
        String endPoint = "/mgmt/api/v1/events";
        String method = "POST";
        
        MultivaluedMap<String, Object> props = new MultivaluedHashMap<>();
        props.put("Content-Type", Arrays.asList("application/json"));
        
        ClientRequestContext requestCtx = mock(ClientRequestContext.class);
        URI uriInfo = new URL("http://localhost:8080/events").toURI();
        when(requestCtx.getHeaders()).thenReturn(props);
        when(requestCtx.getMethod()).thenReturn(method);
        when(requestCtx.getUri()).thenReturn(uriInfo);
        
        // Create a span first.
        Span span = tracer
            .buildSpan("OTLClientRequestFilterTest::testFilterNoSpan")
            .start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            ClientRequestFilter filter = new TracingClientFilter();
            filter.filter(requestCtx);
            
            // Default behavior is "span creation is off", only context will be propagated.
            // Now check the multivalued map if the context data is present.
            // Also the active span should remain the same.
            OTLSpan activeSpan = (OTLSpan)tracer.activeSpan();
            
            assertEquals(span, activeSpan, "No new span should have been created");
            assertEquals("OTLClientRequestFilterTest::testFilterNoSpan", activeSpan.operation(), "Operation must be OTLClientRequestFilterTest::testFilterNoSpan");
            assertTrue(activeSpan.startTime() > 0, "Span Start time must be greater than 0");
            assertEquals(-1L, activeSpan.endTime(), "Span should not have been finished");
            
            int counter = 0;
            for (Map.Entry<String, List<Object>> me : props.entrySet()) {
                String key = me.getKey();
                List<Object> value = me.getValue();
                
                if (key.equals(OTLConstants.TRACE_ID_HEADER)) {
                    assertEquals(value.get(0).toString(), span.context().toTraceId());
                    counter ++;
                }
                else if (key.equals(OTLConstants.SPAN_ID_HEADER)) {
                    assertEquals(value.get(0).toString(), span.context().toSpanId());
                    counter ++;
                }
            }
            assertTrue(counter == 2, "TraceId and SpanId should be propagated");
            
            // Current span does not have any tag.
            Map<String, Object> tags = activeSpan.tags();
            
            // Total number of tags set inside onRequest is 0.
            assertEquals(0, tags.size(), "Decorator should not get called,"
                + " because span creation is disabled");
        }
        finally {
            span.finish();
        }
        flushVisitor();
        
        assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
        assertTrue(tracer.activeSpan() == null, "No active span should be present");
        
        // CAUTION:: We need to reset the value, otherwise, unnecessary span
        //           would be created in subsequent test case.
        System.setProperty("jax.rs.span", "true");
    }
    
    @Test
    public void testRequestNewSpan() throws URISyntaxException, MalformedURLException, IOException {
        String endPoint = "/mgmt/api/v1/events";
        String method = "POST";
        
        MultivaluedMap<String, Object> props = new MultivaluedHashMap<>();
        props.put("Content-Type", Arrays.asList("application/json"));
        
        ClientRequestContext requestCtx = mock(ClientRequestContext.class);
        URI uriInfo = new URL("http://localhost:8080/events").toURI();
        when(requestCtx.getHeaders()).thenReturn(props);
        when(requestCtx.getMethod()).thenReturn(method);
        when(requestCtx.getUri()).thenReturn(uriInfo);
        
        // Create a span first.
        Span span = tracer
            .buildSpan("OTLClientRequestFilterTest::testFilterNewSpan")
            .start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            ClientRequestFilter filter = new TracingClientFilter();
            filter.filter(requestCtx);
            
            // We have enabled new span creation before propagating the context headers.
            OTLSpan activeSpan = null;
            
            try (Scope scope2 = ((OTLSyncScopeManager)tracer.scopeManager()).active()) {
                activeSpan = (OTLSpan)tracer.activeSpan();
            
                assertNotEquals(span, activeSpan, "New span should have been created");
                assertEquals(method, activeSpan.operation(), "Operation must be " + method);
                assertTrue(activeSpan.startTime() > 0, "Span Start time must be greater than 0");
                assertEquals(-1L, activeSpan.endTime(), "Span should not have been finished");
                assertEquals(span.context().toSpanId(), activeSpan.parentSpanId()
                    , span.context().toSpanId() + " must be the parent spanId");

                int counter = 0;
                for (Map.Entry<String, List<Object>> me : props.entrySet()) {
                    String key = me.getKey();
                    List<Object> value = me.getValue();

                    if (key.equals(OTLConstants.TRACE_ID_HEADER)) {
                        assertEquals(value.get(0).toString(), activeSpan.context().toTraceId());
                        counter ++;
                    }
                    else if (key.equals(OTLConstants.SPAN_ID_HEADER)) {
                        assertEquals(value.get(0).toString(), activeSpan.context().toSpanId());
                        counter ++;
                    }
                }
                assertTrue(counter == 2, "TraceId and SpanId should be propagated");

                Map<String, Object> tags = activeSpan.tags();

                // Total number of tags set inside onRequest is 4.
                assertEquals(4, tags.size(), "Span creation is enabled. Decorator must inject 4 tags");

                // Now start checking individual tags.
                Object val = tags.get(Tags.COMPONENT.getKey());
                assertEquals(ClientSpanDecorator.JAX_RS_CLIENT, val.toString()
                    , "Value of tag ["
                        + Tags.COMPONENT.getKey()
                        + "] must be "
                        + ClientSpanDecorator.JAX_RS_CLIENT);

                val = tags.get(Tags.HTTP_METHOD.getKey());
                assertEquals(method, val.toString()
                    , "Value of tag ["
                        + Tags.HTTP_METHOD.getKey()
                        + "] must be "
                        + method);

                val = tags.get(Tags.HTTP_URL.getKey());
                assertEquals("/events", val.toString()
                    , "Value of tag ["
                        + Tags.HTTP_URL.getKey()
                        + "] must be "
                        + "/events");

                val = tags.get(Tags.SPAN_KIND.getKey());
                assertEquals(Tags.SPAN_KIND_CLIENT, val.toString()
                    , "Value of tag ["
                        + Tags.SPAN_KIND.getKey()
                        + "] must be "
                        + Tags.SPAN_KIND_CLIENT);
            }
            finally {
                activeSpan.finish();
            }
        }
        finally {
            span.finish();
        }
        flushVisitor();
        
        assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
        assertTrue(tracer.activeSpan() == null, "No active span should be present");
    }
    
    @Test
    public void testResponseNoSpan() throws URISyntaxException, MalformedURLException, IOException {
        System.setProperty("jax.rs.span", "false");
        String endPoint = "/mgmt/api/v1/events";
        String method = "POST";
        
        MultivaluedMap<String, Object> props = new MultivaluedHashMap<>();
        props.put("Content-Type", Arrays.asList("application/json"));
        
        ClientRequestContext requestCtx = mock(ClientRequestContext.class);
        ClientResponseContext responseCtx = mock(ClientResponseContext.class);
        
        URI uriInfo = new URL("http://localhost:8080/events").toURI();
        when(requestCtx.getHeaders()).thenReturn(props);
        when(requestCtx.getMethod()).thenReturn(method);
        when(requestCtx.getUri()).thenReturn(uriInfo);
        when(responseCtx.getStatus()).thenReturn(200);
        
        // Create a span first.
        Span span = tracer
            .buildSpan("OTLClientResponseFilterTest::testFilter")
            .start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            TracingClientFilter filter = new TracingClientFilter();
            filter.filter(requestCtx);
            
            // Below call will finish the span.
            filter.filter(requestCtx, responseCtx);
            
            // Default behavior is "span creation is on".
            // Now check the multivalued map if the context data is present.
            // Also the active span should remain the same.
            OTLSpan activeSpan = (OTLSpan)tracer.activeSpan();
            
            assertEquals(span, activeSpan, "No new span should have been created");
            assertEquals("OTLClientResponseFilterTest::testFilter", activeSpan.operation(), "Operation must be OTLClientResponseFilterTest::testFilter");
            assertTrue(activeSpan.startTime() > 0, "Span Start time must be greater than 0");
            assertEquals(-1L, activeSpan.endTime(), "Parent span should have been still active");
            
            int counter = 0;
            for (Map.Entry<String, List<Object>> me : props.entrySet()) {
                String key = me.getKey();
                List<Object> value = me.getValue();
                
                if (key.equals(OTLConstants.TRACE_ID_HEADER)) {
                    assertEquals(value.get(0).toString(), span.context().toTraceId());
                    counter ++;
                }
                else if (key.equals(OTLConstants.SPAN_ID_HEADER)) {
                    assertEquals(value.get(0).toString(), span.context().toSpanId());
                    counter ++;
                }
            }
            assertTrue(counter == 2, "TraceId and SpanId should be propagated");
            
            // Current span does not have any tag.
            Map<String, Object> tags = activeSpan.tags();
            
            // Total number of tags set inside onRequest is 0.
            assertEquals(0, tags.size(), "Decorator should not get called,"
                + " because span creation is disabled");
        }
        finally {
            span.finish();
        }
        flushVisitor();
        
        assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
        assertTrue(tracer.activeSpan() == null, "No active span should be present");
        
        // CAUTION:: We need to reset the value, otherwise, unnecessary span
        //           would be created in subsequent test case.
        System.setProperty("jax.rs.span", "true");
    }
    
    @Test
    public void testResponseNewSpan() throws URISyntaxException, MalformedURLException, IOException {
        String endPoint = "/mgmt/api/v1/events";
        String method = "POST";
        
        MultivaluedMap<String, Object> props = new MultivaluedHashMap<>();
        props.put("Content-Type", Arrays.asList("application/json"));
        
        ClientRequestContext requestCtx = mock(ClientRequestContext.class);
        ClientResponseContext responseCtx = mock(ClientResponseContext.class);
        
        URI uriInfo = new URL("http://localhost:8080/events").toURI();
        when(requestCtx.getHeaders()).thenReturn(props);
        when(requestCtx.getMethod()).thenReturn(method);
        when(requestCtx.getUri()).thenReturn(uriInfo);
        when(responseCtx.getStatus()).thenReturn(202);
        
        // Create a span first.
        OTLSpan span = (OTLSpan)tracer
            .buildSpan("OTLClientRequestFilterTest::testFilterNewSpan")
            .start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            TracingClientFilter filter = new TracingClientFilter();
            filter.filter(requestCtx);
            
            // Below call will try to finish the span (if any).
            filter.filter(requestCtx, responseCtx);
            
            // Now the mock visitor instance.
            OTLSpanVisitor visitor = extract();
            if (visitor instanceof MockSpanVisitor) {
                List<OTLSpan> spans = ((MockSpanVisitor)visitor).getSpans();
                assertEquals(1, spans.size(), "Must have one span created");

                OTLSpan closedSpan = spans.get(0);
            
                assertNotEquals(span, closedSpan, "New span should have been created");
                assertEquals(method, closedSpan.operation(), "Operation must be " + method);
                assertTrue(closedSpan.startTime() > 0, "Span Start time must be greater than 0");
                assertTrue(closedSpan.endTime() > closedSpan.startTime(), "Child span should have been finished");
                assertEquals(-1L, span.endTime(), "Parent Span should not have been finished");
                assertEquals(span.context().toSpanId(), closedSpan.parentSpanId()
                    , span.context().toSpanId() + " must be the parent spanId");

                int counter = 0;
                for (Map.Entry<String, List<Object>> me : props.entrySet()) {
                    String key = me.getKey();
                    List<Object> value = me.getValue();

                    if (key.equals(OTLConstants.TRACE_ID_HEADER)) {
                        assertEquals(value.get(0).toString(), closedSpan.context().toTraceId());
                        counter ++;
                    }
                    else if (key.equals(OTLConstants.SPAN_ID_HEADER)) {
                        assertEquals(value.get(0).toString(), closedSpan.context().toSpanId());
                        counter ++;
                    }
                }
                assertTrue(counter == 2, "TraceId and SpanId should be propagated");

                Map<String, Object> tags = closedSpan.tags();

                // Total number of tags set inside onRequest + onResponse is 5.
                assertEquals(5, tags.size(), "Span creation is enabled. Decorator must inject 5 tags");

                // Now start checking individual tags.
                Object val = tags.get(Tags.COMPONENT.getKey());
                assertEquals(ClientSpanDecorator.JAX_RS_CLIENT, val.toString()
                    , "Value of tag ["
                        + Tags.COMPONENT.getKey()
                        + "] must be "
                        + ClientSpanDecorator.JAX_RS_CLIENT);

                val = tags.get(Tags.HTTP_METHOD.getKey());
                assertEquals(method, val.toString()
                    , "Value of tag ["
                        + Tags.HTTP_METHOD.getKey()
                        + "] must be "
                        + method);

                val = tags.get(Tags.HTTP_URL.getKey());
                assertEquals("/events", val.toString()
                    , "Value of tag ["
                        + Tags.HTTP_URL.getKey()
                        + "] must be "
                        + "/events");

                val = tags.get(Tags.SPAN_KIND.getKey());
                assertEquals(Tags.SPAN_KIND_CLIENT, val.toString()
                    , "Value of tag ["
                        + Tags.SPAN_KIND.getKey()
                        + "] must be "
                        + Tags.SPAN_KIND_CLIENT);

                val = tags.get(Tags.HTTP_STATUS.getKey());
                assertEquals(202, Integer.parseInt(val.toString())
                    , "Value of tag ["
                        + Tags.HTTP_STATUS.getKey()
                        + "] must be "
                        + 202);
            }
            else {
                Assertions.fail("Span Visitor must be of type MockSpanVisitor");
            }
        }
        finally {
            span.finish();
        }
        flushVisitor();
        
        assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
        assertTrue(tracer.activeSpan() == null, "No active span should be present");
    }
}
