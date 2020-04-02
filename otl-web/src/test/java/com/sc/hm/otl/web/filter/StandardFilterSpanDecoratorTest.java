/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

import com.sc.hm.otl.core.OTLSpan;
import io.opentracing.tag.Tags;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Sudiptasish Chanda
 */
public class StandardFilterSpanDecoratorTest extends AbstractWebTest {
    
    @Test
    public void testOnRequest() {
        String endPoint = "/ctx/mgmt/api/v1/events";
        String method = "POST";
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURL()).thenReturn(new StringBuffer(endPoint));
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("http_call").start();
        
        FilterSpanDecorator decorator = new StandardFilterSpanDecorator();
        decorator.onRequest(request, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onRequest method is 4.
        assertEquals(4, tags.size()
            , "Total number of tags set by FilterSpanDecorator.onRequest() must be 4");
        
        // Now start checking individual tags.
        Object val = tags.get(Tags.COMPONENT.getKey());
        assertEquals(StandardFilterSpanDecorator.COMPONENT_SERVLET, val.toString()
            , "Value of tag ["
                + Tags.COMPONENT.getKey()
                + "] must be "
                + StandardFilterSpanDecorator.COMPONENT_SERVLET);
        
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
    }
    
    @Test
    public void testOnResponse() {
        int statusCode = 201;
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(statusCode);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("http_call").start();
        
        FilterSpanDecorator decorator = new StandardFilterSpanDecorator();
        decorator.onResponse(request, response, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onRequest method is 4.
        assertEquals(1, tags.size()
            , "Total number of tags set by FilterSpanDecorator.onResponse() must be 1");
        
        // Now start checking individual tags.
        Object val = tags.get(Tags.HTTP_STATUS.getKey());
        assertEquals(statusCode, Integer.parseInt(val.toString())
            , "Value of tag ["
                + Tags.HTTP_STATUS.getKey()
                + "] must be "
                + statusCode);
    }
    
    @Test
    public void testOnError() {
        int statusCode = 500;
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Exception error = new Exception();
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("http_call").start();
        
        FilterSpanDecorator decorator = new StandardFilterSpanDecorator();
        decorator.onError(request, response, error, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onRequest method is 4.
        assertEquals(2, tags.size()
            , "Total number of tags set by FilterSpanDecorator.onError() must be 2");
        
        // Now start checking individual tags.
        Object val = tags.get(Tags.HTTP_STATUS.getKey());
        assertEquals(statusCode, Integer.parseInt(val.toString())
            , "Value of tag ["
                + Tags.HTTP_STATUS.getKey()
                + "] must be "
                + statusCode);
        
        // Now start checking individual tags.
        val = tags.get(Tags.ERROR.getKey());
        assertEquals(Boolean.TRUE, Boolean.valueOf(val.toString())
            , "Value of tag ["
                + Tags.ERROR.getKey()
                + "] must be "
                + Boolean.TRUE);
    }
}
