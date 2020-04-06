/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.RequestCtxDecorator;
import io.opentracing.tag.Tags;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.ext.web.client.HttpResponse;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Sudiptasish Chanda
 */
public class WebClientSpanDecoratorTest extends AbstractUnitTest {
    
    @Test
    public void testOnRequest() {
        String url = "/departments";
        String method = "DELETE";
        
        HttpClientRequest request = mock(HttpClientRequest.class);
        
        when(request.getRawMethod()).thenReturn(method);
        when(request.path()).thenReturn(url);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("web-client").start();
        
        RequestCtxDecorator decorator = new WebClientSpanDecorator();
        decorator.onRequest(request, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onRequest method is 4.
        assertEquals(4, tags.size()
            , "Total number of tags set by WebClientSpanDecorator.onSend() must be 4");
        
        // Now start checking individual tags.
        Object val = tags.get(Tags.COMPONENT.getKey());
        assertEquals(WebClientSpanDecorator.VERTX_CLIENT, val.toString()
            , "Value of tag ["
                + Tags.COMPONENT.getKey()
                + "] must be "
                + EventbusSpanDecorator.SENDER);
        
        val = tags.get(Tags.HTTP_METHOD.getKey());
        assertEquals(method, val.toString()
            , "Value of tag ["
                + Tags.HTTP_METHOD.getKey()
                + "] must be "
                + method);
        
        val = tags.get(Tags.HTTP_URL.getKey());
        assertEquals(url, val.toString()
            , "Value of tag ["
                + Tags.HTTP_URL.getKey()
                + "] must be "
                + method);
        
        val = tags.get(Tags.SPAN_KIND.getKey());
        assertEquals(Tags.SPAN_KIND_CLIENT, val.toString()
            , "Value of tag ["
                + Tags.SPAN_KIND.getKey()
                + "] must be "
                + Tags.SPAN_KIND_CLIENT);
    }
    
    @Test
    public void testOnError() {
        int statusCode = 415;
        
        HttpClientRequest request = mock(HttpClientRequest.class);
        HttpResponse response = mock(HttpResponse.class);
        
        when(response.statusCode()).thenReturn(statusCode);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("web-client").start();
        
        RequestCtxDecorator decorator = new WebClientSpanDecorator();
        decorator.onError(request, response, null, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onResponse method is 2.
        assertEquals(2, tags.size()
            , "Total number of tags set by WebClientSpanDecorator.onResponse() must be 2");
        
        // Now start checking individual tags.
        Object val = tags.get(Tags.ERROR.getKey());
        assertEquals(String.valueOf(Boolean.TRUE), val.toString()
            , "Value of tag ["
                + Tags.ERROR.getKey()
                + "] must be "
                + Boolean.TRUE);
        
        val = tags.get(Tags.HTTP_STATUS.getKey());
        assertEquals(String.valueOf(statusCode), val.toString()
            , "Value of tag ["
                + Tags.HTTP_STATUS.getKey()
                + "] must be "
                + statusCode);
    }
}
