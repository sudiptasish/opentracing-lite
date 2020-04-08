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

import io.opns.otl.core.OTLSpan;
import io.opns.otl.core.RequestCtxDecorator;
import io.opns.otl.util.OTLConstants;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import io.vertx.core.Handler;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.impl.ClientPhase;
import io.vertx.ext.web.client.impl.HttpContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vertx web client interceptor.
 * 
 * <p>
 * for any client request invoked from the client API the web client request filters
 * {@link VertxWebClientInterceptor} are executed that could manipulate the request. 
 * If not aborted, the outgoing request is then physically sent over to the server
 * side. Because vertx is asynchronous, therefore the caller has to set the
 * relevant span context before invoking the web client API.
 * 
 * <p>
 * The client request filter will always create a new span to pass the newly
 * created span context (namely the traceId, spanId and the baggage items, if any)
 * to the next service on the chain. Because, vertx web client is asynchronous
 * therefore, it acts like any asynchronous message producer. The span that the
 * interceptor will create will be closed immediately in the current thread context.
 * But unlike any message consumer, the vertx service, on the other side will start
 * a CHILD_OF span (instead of FOLLOWS_FROM).
 * 
 * User, however, can override the default behavior by specifying the system property
 * <code>-Dvertx.webclient.span</code>  as false. In which case, no new span will
 * be created, but only the contextual data will be passed.
 * 
 * <p>
 * Web Client should be created once on application startup and then reused.
 * Below code shows the proper way of initializing a {@link WebClient}.
 * 
 * <pre>
 * {@code
 *     .....
 * 
 *     public WebClient getClient() {
 *         WebClient client = WebClient
 *             .create(vertx, new WebClientOptions()
 *                 .setSsl(false)
 *                 .setTrustAll(true)
 *                 .setVerifyHost(false)
 *                 .setMaxPoolSize(...)
 *                 ....
 *                 ....);
 *       
 *         ((WebClientInternal)client)
 *             .addInterceptor(new VertxWebClientInterceptor());
 *     } 
 *     ....
 *     ....
 * 
 *     public void invoke(RoutingContext ctx, String port, String endpoint, String uri) {
 *         Client client = getClient();
 * 
 *         Span span = TraacingVtxHandler.currentSpan(ctx);
 *  
 *         try (Scope scope = tracer.activateSpan(span)) {
 *              
 *             // Send Request to next service.
 *             JsonObject json = new JsonObject();
 *             json.put("id", "...");
 *             json.put("name", "....");
 *
 *             client.post(port, endpoint, uri)
 *                 .sendJson(json, new ResponseHandler(ctx));
 *         }
 *     }
 * }
 * </pre>
 *
 * @author Sudiptasish Chanda
 */
public class VertxWebClientInterceptor implements Handler<HttpContext<?>> {
    
    private final Logger logger = LoggerFactory.getLogger(VertxWebClientInterceptor.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<RequestCtxDecorator> decorators = new ArrayList<>(1);
    
    // Map to store all the spans that were created during SEND_REQUEST phase,
    // and waiting for a response from remote service.
    // Once a response is received, i.e., in RECEIVE_RESPONSE phase, the span
    // will be removed from this map.
    private final ConcurrentMap<String, Span> inTransitSpans = new ConcurrentHashMap<>();
    
    public VertxWebClientInterceptor() {
        decorators.add(new WebClientSpanDecorator());
    }

    @Override
    public void handle(HttpContext<?> httpContext) {
        Boolean enableSpan = Boolean.parseBoolean(
                    System.getProperty("vertx.webclient.span", "true"));
        
        try {
            if (httpContext.phase() == ClientPhase.SEND_REQUEST) {
                // This methid will be called from WebClient.sendXXX( ... ) 
                // or WebClient.send( ... , Handler) methods.

                if (logger.isTraceEnabled()) {
                    logger.trace("Value of vertx.webclient.span is: " + enableSpan);
                }
                if (enableSpan) {
                    handleActiveSpan(httpContext);
                }
                else {
                    sendContextData(httpContext);
                }
            }
            else if (httpContext.phase() == ClientPhase.RECEIVE_RESPONSE) {
                // The HttpClientRequest already has the spanId in it's header.
                // Remember that it was injected while sending the request in SEND_REQUEST phase.
                if (enableSpan) {
                    Span clientSpan = inTransitSpans.remove(httpContext
                        .clientRequest()
                        .headers()
                        .get(OTLConstants.SPAN_ID_HEADER));

                    for (RequestCtxDecorator decorator : decorators) {
                        decorator.onResponse(httpContext.clientRequest()
                            , httpContext.clientResponse()
                            , clientSpan);
                    }
                    clientSpan.finish();
                    if (logger.isTraceEnabled()) {
                        logger.trace("Finished the vertx web interceptor client span. Error: False");
                    }
                }
            }
        }
        catch (Throwable e) {
            logger.error("Error in WertxWebClientInterceptor", e);
            if (enableSpan) {
                Span clientSpan = inTransitSpans.remove(httpContext
                    .clientRequest()
                    .headers()
                    .get(OTLConstants.SPAN_ID_HEADER));

                for (RequestCtxDecorator decorator : decorators) {
                    decorator.onError(httpContext.clientRequest()
                        , httpContext.clientResponse()
                        , e
                        , clientSpan);
                }
                clientSpan.finish();
                if (logger.isTraceEnabled()) {
                    logger.trace("Finished the vertx web interceptor client span. Error: True");
                }
            }
        }
        // Call the next interceptor in the chain.
        httpContext.next();
    }
    
    /**
     * Api to create a span and store it in the transit map.
     * @param httpContext 
     */
    private void handleActiveSpan(HttpContext<?> httpContext) {
        // The currently active span would automatically become the parent 
        // span of this newly created span.
        // The active span will be availableonly during SEND_REQUEST phase.
        // Once the response from remote service is received, that part
        // will be handled by a different thread.
        OTLSpan activeSpan = (OTLSpan)tracer.activeSpan();
        if (activeSpan == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("No active span present. No context info will be propagated");
            }
            return;
        }
        Span clientSpan = tracer
            .buildSpan("vertx-web-send")
            .asChildOf(activeSpan)
            .start();

        if (logger.isTraceEnabled()) {
            logger.trace("Created new vertx client span: {}", clientSpan.context());
        }

        tracer.inject(clientSpan.context()
            , Format.Builtin.TEXT_MAP_INJECT
            , new VertxMsgContextAdapter(httpContext.clientRequest().headers()));

        for (RequestCtxDecorator decorator : decorators) {
            decorator.onRequest(httpContext.clientRequest(), clientSpan);
        }
        // Note that we are not activating the childSpan, it will be added to
        // the async scope only. At this stage the old span is still active.
        // The idea behind adding it to the async scope is, so that the 
        // {@link TracingResponseHandler} can retrieve it and finish it.
        inTransitSpans.put(clientSpan.context().toSpanId(), clientSpan);
    }
    
    /**
     * If span creation is disabled, then send only the context data.
     * @param httpContext 
     */
    private void sendContextData(HttpContext<?> httpContext) {
        // Span creation is disabled. Therefore just extract the span context
        // from the currently active span and propagate.
        // If no active span, then nothing will be propagated.
        // Note that, this behavior can be confusing, because the downstream system, at
        // the other side will receive the contextual info, and it may treat
        // that as a followup span created by client. Therefore it is
        // essential to inject a special header "no.span=true" in order for
        // the vertx/other server to distinguish it easily.

        // The flag, -Dvertx.webclient.span should be set to false only if user 
        // experiences a sudden influx of span.
        Span span = tracer.activeSpan();

        if (span != null) {
            tracer.inject(span.context()
                , Format.Builtin.TEXT_MAP_INJECT
                , new VertxMsgContextAdapter(httpContext.request().headers()));

            if (logger.isTraceEnabled()) {
                logger.trace("Span creation is disabled in vertx web client. Therefore propagating the"
                    + " current contextual info from the span: {}", span.context().toSpanId());
            }
        }
        else {
            if (logger.isTraceEnabled()) {
                logger.trace("No Active span exist. Context won't be propagated");
            }
        }
    }
}
