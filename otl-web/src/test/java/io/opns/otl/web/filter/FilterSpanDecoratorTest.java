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
import io.opns.otl.core.OTLSpan;
import io.opns.otl.core.RequestCtxDecorator;
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
public class FilterSpanDecoratorTest extends AbstractUnitTest {
    
    @Test
    public void testOnRequest() {
        String endPoint = "/ctx/mgmt/api/v1/events";
        String method = "POST";
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURL()).thenReturn(new StringBuffer(endPoint));
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("http_call").start();
        
        RequestCtxDecorator decorator = new FilterSpanDecorator();
        decorator.onRequest(request, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onRequest method is 4.
        assertEquals(4, tags.size()
            , "Total number of tags set by FilterSpanDecorator.onRequest() must be 4");
        
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
    }
    
    @Test
    public void testOnResponse() {
        int statusCode = 201;
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(statusCode);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("http_call").start();
        
        RequestCtxDecorator decorator = new FilterSpanDecorator();
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
        
        RequestCtxDecorator decorator = new FilterSpanDecorator();
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
