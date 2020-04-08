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

import io.opns.otl.vertx.TracingVtxHandler;
import io.opns.otl.util.OTLConstants;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class WebClientInterceptorTest extends AbstractUnitTest {
    
    @Test
    public void testSend() {
        String uri = "/ctx/api/v1/employees";
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        headers.put(OTLConstants.BAGGAGE_ITEMS_HEADER + "CorrelationId", "ABCDEF");
        
        int statusCode = 201;
        boolean failed = false;
        
        CountDownLatch latch = new CountDownLatch(1);
        
        HttpServerRequest request = new MockHttpServerRequest(uri, method, headers);
        HttpServerResponse response = new MockHttpServerResponse(statusCode);

        RoutingContext context = new MockRoutingContext(request, response, failed, false, latch);
        
        TracingVtxHandler vtxHandler = new TracingVtxHandler();
        vtxHandler.handle(context);
        
        // Now get a webclient.
        
    }
}
