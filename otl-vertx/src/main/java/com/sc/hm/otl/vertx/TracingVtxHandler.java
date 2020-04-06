/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.OTLAsyncScope;
import com.sc.hm.otl.core.RequestCtxDecorator;
import com.sc.hm.otl.core.impl.OTLAsyncScopeImpl;
import com.sc.hm.otl.core.impl.OTLTracer;
import com.sc.hm.otl.util.OTLConstants;
import com.sc.hm.otl.util.ObjectCreator;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracing handler middleware for Vertx.
 * 
 * <p>
 * From vertx doc, A Router is one of the core concepts of Vert.x-Web. It’s an 
 * object which maintains zero or more Routes. A router typically takes an HTTP
 * request and finds the first matching route for that request, and passes the 
 * request to that route.
 * 
 * By default routes are matched in the order they are added to the router.
 * When a request arrives the router will step through each route and check if it
 * matches, if it matches then the handler for that route will be called. If the
 * handler subsequently calls next the handler for the next matching route (if any)
 * will be called. And so on...
 * 
 * <p>
 * The {@link TracingVtxHandler} acts as a filter to intercept the incoming request
 * and therefore sets tracing context. So we need to ensure this handler is added
 * at the very beginning of the router. This is done like below:
 * 
 * <pre>
 * {@code
 *     .....
 * 
 *     Router router = Router.router(vertx);
 *     router.route().order(-1)
 *          .handler(new TracingVtxHandler())
 *          
 * }
 * </pre>
 * 
 * You can also pass any custom decorator and skip pattern at the time of creating
 * and registering the tracing handler.
 * 
 * <pre>
 * {@code
 *     .....
 * 
 *     Router router = Router.router(vertx);
 *     router.route().order(-1)
 *          .handler(new TracingVtxHandler("decorator.class.name", "utl.skip.pattern"))
 *          
 * }
 * </pre>
 * 
 * This will ensure the tracing handler is called for all kind of request.
 *
 * @author Sudiptasish Chanda
 */
public class TracingVtxHandler implements Handler<RoutingContext> {
    
    private static final Logger logger = LoggerFactory.getLogger(TracingVtxHandler.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<RequestCtxDecorator> decorators = new ArrayList<>(1);
    private final List<Pattern> skipPatterns = new ArrayList<>();
    
    public TracingVtxHandler() {
        this("", "");
    }

    /**
     * Developer can pass any custom decorator and skip pattern at the time of
     * instantiating the tracing handler. The decorator will be added along with
     * the default {@link MiddlewareSpanDecorator} decorator.
     * 
     * @param decoratorClass
     * @param skipPattern 
     */
    public TracingVtxHandler(String decoratorClass, String skipPattern) {
        decorators.add(new MiddlewareSpanDecorator());
        
        if (decoratorClass != null && (decoratorClass = decoratorClass.trim()).length() > 0) {
            // Initialize and add the decorator.
            decorators.add(initDecorator(decoratorClass));
        }
        
        if (skipPattern != null && (skipPattern = skipPattern.trim()).length() > 0) {
            String[] patterns = skipPattern.split(",");
            for (String pattern : patterns) {
                skipPatterns.add(Pattern.compile(pattern.trim()));
            }
        }
    }

    @Override
    public void handle(RoutingContext routingCtx) {
        if (!isTraceable(routingCtx)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Request with uri [{}] is not traceable"
                    , routingCtx.request().absoluteURI());
            }
            routingCtx.next();
            return;
        }
        if (routingCtx.failed()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Routing context failure.");
            }
            TracingHelper.windUp(routingCtx, decorators, true);
            
            routingCtx.next();
        }
        else {
            // Now start tracing the request.
            // Create a new span after extracting the span context from the request.
            SpanContext context = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT
                , new VertxMsgContextCarrier(routingCtx.request()));

            // Create the span.
            // Creation of span does not necessarily create the scope. Scope indicates
            // the work being done by the current thread at any given point of time.
            // And if that work is to represented by a span, then the span should be
            // explicitly set in the current scope before executing the task.
            Span span = ((OTLTracer.SpanBuilder)tracer
                .buildSpan(routingCtx.request().method().name()))
                .asChildOf(context)
                .withCallback(routingCtx)
                .ignoreActiveSpan()
                .start();
            
            // This will not put the scope in the current threadlocal.
            // Async Scope should be created only once per request. It will act as
            // a placeholder for multiple spans.
            OTLAsyncScope asyncScope = new OTLAsyncScopeImpl();
            asyncScope.add(span);
            routingCtx.put(OTLConstants.VERTX_SCOPE, asyncScope);

            if (logger.isTraceEnabled()) {
                logger.trace("Vertx Tracing handler created new Span: {}", span.context());
            }

            for (RequestCtxDecorator decorator : decorators) {
                decorator.onRequest(routingCtx.request(), span);
            }
            // Because vertx request-response is async (event loop), therefore the scope
            // cannot be kept in current threadlocal. The same thread would be handling
            // multiple client requests, thus there is a hig chance of overwriting the
            // old context/scope with new value.
            // Also, as of today the contextual info cannot be passed from one thread to other.
            // Hence, it is best to manually propagate the context data.
            routingCtx.put(OTLConstants.VERTX_SCOPE, asyncScope);
            
            // A body end handler will be attached to Every request.
            // This will be invoked once the final response is sent to client.
            // Only then, should it close the span.
            // In case of an exception, the closing of the span will be taken care
            // of by the error handler.
            routingCtx.addBodyEndHandler(new RoutingEndHandler(routingCtx));

            // Call the next handler (middleware) on the request chain.
            routingCtx.next();
        }
    }
    
    /**
     * Remove and return the current active span from the routing context.
     * 
     * @param routingCtx
     * @return Span
     */
    public static Span removeActiveSpan(RoutingContext routingCtx) {
        OTLAsyncScope scope = scope(routingCtx);
        if (scope != null) {
            return scope.removeCurrent();
        }
        return null;
    }
    
    /**
     * Return the current span from the routing context.
     * 
     * @param routingCtx
     * @return Span
     */
    public static Span activeSpan(RoutingContext routingCtx) {
        OTLAsyncScope scope = scope(routingCtx);
        if (scope != null) {
            return scope.active();
        }
        return null;
    }
    
    /**
     * Return the current scope from the routing context.
     * 
     * @param routingCtx
     * @return Span
     */
    public static OTLAsyncScope scope(RoutingContext routingCtx) {
        return routingCtx.get(OTLConstants.VERTX_SCOPE);
    }

    /**
     * Create and initialize the custom span decorator. This span decorator will
     * be added after {@code StandardFilterSpanDecorator} and
     * platform provided decorator {@code FilterSpanDecorator}.
     *
     * @param decoratorClass Custom decorator class.
     * @return FilterSpanDecorator
     */
    private RequestCtxDecorator initDecorator(String decoratorClass) {
        return ObjectCreator.create(decoratorClass);
    }

    /**
     * Check if the current URL path needs to be traced.
     * 
     * @param request
     * @param response
     * @return boolean
     */
    private boolean isTraceable(RoutingContext routingCtx) {
        if (!skipPatterns.isEmpty()) {
            boolean skipped = false;
            
            String uri = routingCtx.request().path();
            for (Pattern skipPattern : skipPatterns) {
                skipped = skipPattern.matcher(uri).matches();
                if (skipped) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }
}
