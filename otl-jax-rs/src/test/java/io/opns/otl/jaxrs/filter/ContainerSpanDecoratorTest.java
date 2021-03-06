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
package io.opns.otl.jaxrs.filter;

import io.opns.otl.jaxrs.filter.ContainerSpanDecorator;
import io.opns.otl.core.OTLSpan;
import io.opns.otl.core.RequestCtxDecorator;
import io.opentracing.Scope;
import io.opentracing.tag.Tags;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.UriInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Sudiptasish Chanda
 */
public class ContainerSpanDecoratorTest extends AbstractUnitTest {
    
    @Test
    public void testOnRequest() {
        String endPoint = "/ctx/mgmt/api/v1/events";
        String method = "POST";
        
        ContainerRequestContext requestCtx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        when(requestCtx.getMethod()).thenReturn(method);
        when(requestCtx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPath()).thenReturn("/events");
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("jaxrs_call").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            RequestCtxDecorator decorator = new ContainerSpanDecorator();
            decorator.onRequest(requestCtx, span);

            Map<String, Object> tags = span.tags();

            // Total number of tags set inside onRequest method is 4.
            assertEquals(4, tags.size()
                , "Total number of tags set by ContainerSpanDecorator.onRequest() must be 4");

            // Now start checking individual tags.
            Object val = tags.get(Tags.COMPONENT.getKey());
            assertEquals(ContainerSpanDecorator.JAX_RS, val.toString()
                , "Value of tag ["
                    + Tags.COMPONENT.getKey()
                    + "] must be "
                    + ContainerSpanDecorator.JAX_RS);

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
        }
        finally {
            span.finish();
        }
        flushVisitor();
    }
    
    @Test
    public void testOnResponse() {
        int statusCode = 201;
        
        ContainerRequestContext requestCtx = mock(ContainerRequestContext.class);
        ContainerResponseContext responseCtx = mock(ContainerResponseContext.class);
        when(responseCtx.getStatus()).thenReturn(statusCode);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("jaxrs_call").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            RequestCtxDecorator decorator = new ContainerSpanDecorator();
            decorator.onResponse(requestCtx, responseCtx, span);

            Map<String, Object> tags = span.tags();

            // Total number of tags set inside onRequest method is 4.
            assertEquals(1, tags.size()
                , "Total number of tags set by ContainerSpanDecorator.onResponse() must be 1");

            // Now start checking individual tags.
            Object val = tags.get(Tags.HTTP_STATUS.getKey());
            assertEquals(statusCode, Integer.parseInt(val.toString())
                , "Value of tag ["
                    + Tags.HTTP_STATUS.getKey()
                    + "] must be "
                    + statusCode);
        }
        finally {
            span.finish();
        }
        flushVisitor();
    }
}
