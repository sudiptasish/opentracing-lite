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
package io.opns.otl.web.filter;

import io.opns.otl.web.filter.FilterSpanDecorator;
import io.opns.otl.web.filter.TracingWebFilter;
import io.opns.otl.core.OTLSpan;
import io.opns.otl.core.OTLSpanVisitor;
import io.opns.otl.core.OTLSyncScopeManager;
import io.opns.otl.core.RequestCtxDecorator;
import io.opns.otl.util.OTLConstants;
import io.opentracing.tag.Tags;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 *
 * @author Sudiptasish Chanda
 */
public class TracingWebFilterTest extends AbstractUnitTest {
    
    @Test
    public void testInit() throws ServletException {
        String decoratorClass = "io.opns.otl.web.filter.MockOTLFilterDecorator";
        String skipPattern = "/events";
        ServletContext mockCtx = new MockServletContext();
        
        TracingWebFilter filter = new TracingWebFilter();
        FilterConfig config = mock(FilterConfig.class);
        
        when(config.getServletContext()).thenReturn(mockCtx);
        when(config.getInitParameter(OTLConstants.DECORATOR)).thenReturn(decoratorClass);
        when(config.getInitParameter(OTLConstants.SKIP_PATTERN)).thenReturn(skipPattern);
        
        filter.init(config);
        
        // Check the servlet context whether the decorator and skip pattern is set.
        List<RequestCtxDecorator> decorators = (List<RequestCtxDecorator>)
            mockCtx.getAttribute("otl.filter.decorator");
        List<Pattern> skipPatterns = (List<Pattern>)mockCtx.getAttribute("otl.skip.pattern");
        
        assertNotNull(decorators, "Filter span decorator must be set");
        assertNotNull(skipPatterns, "Skip Pattern must be set");
        
        // Validate the decorators.
        assertEquals(2, decorators.size(), "Total number of decorators must be 2");
        RequestCtxDecorator decorator_1 = decorators.get(0);
        RequestCtxDecorator decorator_2 = decorators.get(1);
        
        assertTrue(decorator_1 instanceof FilterSpanDecorator
            , "Decorator 1 must be of type FilterSpanDecorator");
        assertTrue(decorator_2 instanceof MockOTLFilterDecorator
            , "Decorator 2 must be of type MockOTLFilterDecorator");
        
        // Validate the skip pattern.
        assertEquals(1, skipPatterns.size(), "Total number of decorators must be 1");
        Pattern pattern = skipPatterns.get(0);
        
        assertEquals(skipPattern, pattern.toString(), "Skip pattern must be " + skipPattern);
    }
    
    @Test
    public void testDoFilter() throws IOException, ServletException {
        String endPoint = "/mgmt/api/v1/events";
        String method = "POST";
        ServletContext mockCtx = new MockServletContext();
        
        Map<String, String> props = new HashMap<>();
        props.put("Content-Type", "application/json");
        
        FilterConfig config = mock(FilterConfig.class);
        when(config.getServletContext()).thenReturn(mockCtx);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = new MockFilterChain();
        
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURL()).thenReturn(new StringBuffer(endPoint));
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(props.keySet()));
        when(request.getHeader("Content-Type")).thenReturn(props.get("Content-Type"));
        when(response.getStatus()).thenReturn(200);
        
        TracingWebFilter filter = new TracingWebFilter();
        filter.init(config);
        filter.doFilter(request, response, chain);
        
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
            assertTrue(span.endTime() > span.startTime(), "Span End time must be greater than Start time");
            
            Map<String, Object> tags = span.tags();
            // Total number of tags set inside onRequest & onResponse method is 5.
            assertEquals(5, tags.size()
                , "Total number of tags set by FilterSpanDecorator.onRequest() must be 5");

            // Now start checking individual tags.
            Object val = tags.get(Tags.COMPONENT.getKey());
            assertEquals(FilterSpanDecorator.WEB_SERVLET, val.toString()
                , "Value of tag ["
                    + Tags.COMPONENT.getKey()
                    + "] must be "
                    + FilterSpanDecorator.WEB_SERVLET);

            val = tags.get(Tags.HTTP_METHOD.getKey());
            assertEquals(method, val.toString()
                , "Value of tag ["
                    + Tags.HTTP_METHOD.getKey()
                    + "] must be "
                    + method);

            val = tags.get(Tags.HTTP_URL.getKey());
            assertEquals(endPoint, val.toString()
                , "Value of tag ["
                    + Tags.HTTP_URL.getKey()
                    + "] must be "
                    + endPoint);

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
    
    @Test
    public void testSkipDoFilter() throws IOException, ServletException {
        String endPoint = "/mgmt/api/v1/events";
        String skipPattern = "/events";
        
        ServletContext mockCtx = new MockServletContext();
        
        FilterConfig config = mock(FilterConfig.class);
        when(config.getServletContext()).thenReturn(mockCtx);
        when(config.getInitParameter(OTLConstants.SKIP_PATTERN)).thenReturn(skipPattern);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/mgmt/api/v1");
        when(request.getRequestURI()).thenReturn(endPoint);
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = new MockFilterChain();
        
        TracingWebFilter filter = new TracingWebFilter();
        filter.init(config);
        filter.doFilter(request, response, chain);
        
        // Now the mock visitor instance.
        OTLSpanVisitor visitor = extract();
        if (visitor instanceof MockSpanVisitor) {
            List<OTLSpan> spans = ((MockSpanVisitor)visitor).getSpans();
            assertEquals(0, spans.size(), "Must not have any span created");
        }
        else {
            Assertions.fail("Span Visitor must be of type MockSpanVisitor");
        }
    }
}
