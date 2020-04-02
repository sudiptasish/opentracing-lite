/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.OTLSyncScopeManager;
import io.opentracing.tag.Tags;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLResponseFilterTest extends AbstractFilterTest {
    
    @Test
    public void testFilter() throws IOException, ServletException {
        String endPoint = "/mgmt/api/v1/events";
        String method = "POST";
        
        MultivaluedMap<String, String> props = new MultivaluedHashMap<>();
        props.put("Content-Type", Arrays.asList("application/json"));
        
        ContainerRequestContext requestCtx = mock(ContainerRequestContext.class);
        ContainerResponseContext responseCtx = mock(ContainerResponseContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        when(requestCtx.getHeaders()).thenReturn(props);
        when(requestCtx.getMethod()).thenReturn(method);
        when(requestCtx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPath()).thenReturn("/events");
        when(responseCtx.getStatus()).thenReturn(200);
        
        OTLRequestFilter filter1 = new OTLRequestFilter();
        filter1.filter(requestCtx);
        
        // The below call will ensure the span is finished, scope is closed.
        OTLResponseFilter filter2 = new OTLResponseFilter();
        filter2.filter(requestCtx, responseCtx);
        
        // Now the mock visitor instance.
        OTLSpanVisitor visitor = extract();
        if (visitor instanceof MockSpanVisitor) {
            List<OTLSpan> spans = ((MockSpanVisitor)visitor).getSpans();
            assertEquals(1, spans.size(), "Must have one span created");
            
            OTLSpan span = spans.get(0);
            
            assertNotNull(span.context().toTraceId(), "TraceId must be non null");
            assertNotNull(span.context().toSpanId(), "SpanId must be non null");
            assertEquals(method, span.operation(), "Operation must be " + method);
            assertTrue(span.startTime() > 0, "Span Start time must be greater than 0");
            assertTrue(span.endTime() >= span.startTime(), "Span End time must be greater than Start time");
            
            Map<String, Object> tags = span.tags();
            // Total number of tags set inside onRequest + onResponse is 5.
            assertEquals(5, tags.size()
                , "Total number of tags set by StandardJaxRsFilterSpanDecoratorTest"
                    + ".onRequest()/onResponse() must be 5");

            // Now start checking individual tags.
            Object val = tags.get(Tags.COMPONENT.getKey());
            assertEquals(StandardJaxRsFilterSpanDecorator.COMPONENT_JAX_RS, val.toString()
                , "Value of tag ["
                    + Tags.COMPONENT.getKey()
                    + "] must be "
                    + StandardJaxRsFilterSpanDecorator.COMPONENT_JAX_RS);

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
            assertEquals(Tags.SPAN_KIND_SERVER, val.toString()
                , "Value of tag ["
                    + Tags.SPAN_KIND.getKey()
                    + "] must be "
                    + Tags.SPAN_KIND_SERVER);

            val = tags.get(Tags.HTTP_STATUS.getKey());
            assertEquals(200, Integer.parseInt(val.toString())
                , "Value of tag ["
                    + Tags.HTTP_STATUS.getKey()
                    + "] must be "
                    + 200);
            
            spans.clear();
            
            assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
            assertTrue(tracer.activeSpan() == null, "No active span should be present");
        }
        else {
            Assertions.fail("Span Visitor must be of type MockSpanVisitor");
        }
    }
}
