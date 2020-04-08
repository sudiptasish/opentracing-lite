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
package io.opns.otl.vertx;

import io.opns.otl.vertx.EventbusSpanDecorator;
import io.opns.otl.vertx.WebClientSpanDecorator;
import io.opns.otl.core.OTLSpan;
import io.opns.otl.core.RequestCtxDecorator;
import io.opentracing.tag.Tags;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
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
        
        when(request.method()).thenReturn(HttpMethod.DELETE);
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
        HttpClientResponse response = mock(HttpClientResponse.class);
        
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
