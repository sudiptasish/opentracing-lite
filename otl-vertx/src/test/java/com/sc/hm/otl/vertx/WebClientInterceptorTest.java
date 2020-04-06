/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.util.OTLConstants;
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
