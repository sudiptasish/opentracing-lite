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
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.RequestCtxDecorator;
import io.opentracing.tag.Tags;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Sudiptasish Chanda
 */
public class MiddlewareSpanDecoratorTest extends AbstractUnitTest {
    
    @Test
    public void testOnRequest() {
        String url = "/employees";
        String method = "GET";
        
        HttpServerRequest request = mock(HttpServerRequest.class);
        
        when(request.rawMethod()).thenReturn(method);
        when(request.path()).thenReturn(url);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("web-server").start();
        
        RequestCtxDecorator decorator = new MiddlewareSpanDecorator();
        decorator.onRequest(request, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onRequest method is 4.
        assertEquals(4, tags.size()
            , "Total number of tags set by MiddlewareSpanDecorator.onRequest() must be 4");
        
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
        assertEquals(url, val.toString()
            , "Value of tag ["
                + Tags.HTTP_URL.getKey()
                + "] must be "
                + method);
        
        val = tags.get(Tags.SPAN_KIND.getKey());
        assertEquals(Tags.SPAN_KIND_SERVER, val.toString()
            , "Value of tag ["
                + Tags.SPAN_KIND.getKey()
                + "] must be "
                + Tags.SPAN_KIND_SERVER);
    }
    
    @Test
    public void testOnResponse() {
        int statusCode = 200;
        
        HttpServerRequest request = mock(HttpServerRequest.class);
        HttpServerResponse response = mock(HttpServerResponse.class);
        
        when(response.getStatusCode()).thenReturn(statusCode);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("web-server").start();
        
        RequestCtxDecorator decorator = new MiddlewareSpanDecorator();
        decorator.onResponse(request, response, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onError method is 1.
        assertEquals(1, tags.size()
            , "Total number of tags set by MiddlewareSpanDecorator.onResponse() must be 1");
        
        // Now start checking individual tags.
        Object val = tags.get(Tags.HTTP_STATUS.getKey());
        assertEquals(String.valueOf(statusCode), val.toString()
            , "Value of tag ["
                + Tags.HTTP_STATUS.getKey()
                + "] must be "
                + statusCode);
    }
    
    @Test
    public void testOnError() {
        int statusCode = 415;
        
        HttpServerRequest request = mock(HttpServerRequest.class);
        HttpServerResponse response = mock(HttpServerResponse.class);
        
        when(response.getStatusCode()).thenReturn(statusCode);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("web-server").start();
        
        RequestCtxDecorator decorator = new MiddlewareSpanDecorator();
        decorator.onError(request, response, null,span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onError method is 2.
        assertEquals(2, tags.size()
            , "Total number of tags set by MiddlewareSpanDecorator.onError() must be 2");
        
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
