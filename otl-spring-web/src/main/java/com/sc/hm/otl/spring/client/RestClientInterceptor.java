/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.spring.client;

import com.sc.hm.otl.core.RequestCtxDecorator;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * Spring Rest template interceptor.
 * 
 * <p>
 * This interceptor must be used to intercept client-side HTTP requests. Implementations
 * of this interface can be registered with the {@link RestTemplate}, so as to 
 * modify the outgoing {@link HttpRequest} in order to inject the tracing headers.
 * 
 * <p>
 * As of today, the following headers will be injected:
 * 
 * <li>X-B3-TraceId</li>
 * <li>X-B3-SpanId</li>
 * <li>X-B3-Sampled</li>
 * <li>X-B3-Baggage-<key></li>
 * 
 * If there is an active span in the current thread context, then this above 
 * information will be extracted from the span and injected into the {@link HttpRequest}
 * object.
 * 
 * <p>
 * Note that, by default, an interceptor never creates a new span while propagating
 * the B3 headers. It is assumed that the network call is part of the responsibility
 * of this service's current work, and thus, is part of the currently active span.
 * 
 * You can, however, override this rule by passing the system property 
 * <code>-Drest.template.span</code> as true. It always ensure a new span is created
 * before making an outbound call, and post receiving the response, the span will
 * be closed.
 * 
 * The interceptor of a RestTemplate is added via the custom bean post processor,
 * provided the rest template is created via traditional @Bean API.
 * <pre>
 * {@code 
 *     @Bean
 *     public RestTemplate defaultRestTemplate() {
 *         return new RestTemplate(); 
 *     }
 * }
 * </pre>
 * 
 * RestTemplate created using the above method will ensure the invoke the custom
 * bean post processor, which will ensure the appropriate interceptor is added.
 * 
 * <p>
 * Caution: The following code will never add the interceptor, and thus should be
 * avoided. Note that spring {@link RestTemplate) is thread safe. This means, for 
 * instance, that the RestTemplate should be constructed only once and reused.
 * You can also use callbacks to customize its operations.
 * 
 * <pre>
 * {@code 
 *     public void callAPI(String url) {
 *         RestTemplate template = return new RestTemplate();
 *         template.....
 *     }
 * }
 * </pre>
 *
 * @author Sudiptasish Chanda
 */
public class RestClientInterceptor implements ClientHttpRequestInterceptor {
    
    private final Logger logger = LoggerFactory.getLogger(RestClientInterceptor.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<RequestCtxDecorator> decorators = Arrays.asList(new TemplateSpanDecorator());

    @Override
    public ClientHttpResponse intercept(HttpRequest request
        , byte[] bytes
        , ClientHttpRequestExecution exec) throws IOException {
        
        ClientHttpResponse response = null;
        Boolean enableSpan = Boolean.getBoolean("rest.template.span");
        
        if (logger.isTraceEnabled()) {
            logger.trace("Value of rest.template.span is: " + enableSpan);
        }
        if (enableSpan) {
            // The currently active span would become the parent span of this
            // newly created span.
            Span span = tracer.buildSpan(request.getMethod().toString()).start();
            
            if (logger.isTraceEnabled()) {
                logger.trace("Created new Rest template span: {}", span.context());
            }
            tracer.inject(span.context()
                , Format.Builtin.TEXT_MAP_INJECT
                , new HttpHeaderAdapter(request.getHeaders()));

            for (RequestCtxDecorator decorator : decorators) {
                decorator.onRequest(request, span);
            }
            // Call the next interceptor on the request chain, or the remote service.
            // Once the control comes back to this interceptor, the scope will be closed.
            // As expected the corresponding span won't be finished. Developer has to
            // explicitly call span.finish() to complete the span.
            try (Scope scope = tracer.activateSpan(span)) {
                // At this stage the current scope is set in the thread context, 
                // and existing scope has become dormant, which will be activated
                // later once the span is finished, thus removed from the current
                // thread context. At this stage, the existing span become the
                // parent span.
                response = exec.execute(request, bytes);

                for (RequestCtxDecorator decorator : decorators) {
                    decorator.onResponse(request, response, span);
                }
            }
            catch (Exception e) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Error occured while executing rest template span: {}. Error Msg: {}"
                        , span.context()
                        , e.getMessage());
                }
                for (RequestCtxDecorator decorator : decorators) {
                    decorator.onError(request, response, e, span);
                }
            }
            finally {
                span.finish();
                if (logger.isTraceEnabled()) {
                    logger.trace("Finished the Rest template span: {}", span.context());
                }
            }
        }
        else {
            // Default behavior.
            // Span creation isdisabled. Therefore just extract the span context
            // from the currently active span and propagate.
            // If no active span, then nothing will be propagated.
            Span span = tracer.activeSpan();
            if (span != null) {
                tracer.inject(span.context()
                    , Format.Builtin.TEXT_MAP_INJECT
                    , new HttpHeaderAdapter(request.getHeaders()));
                
                if (logger.isTraceEnabled()) {
                    logger.trace("Span creation is disabled in Rest template. Therefore propagating the"
                        + " current contextual info from the span: {}", span.context().toSpanId());
                }
            }
            else {
                if (logger.isTraceEnabled()) {
                    logger.trace("No Active span exist. Context won't be propagated");
                }
            }
            response = exec.execute(request, bytes);
        }
        return response;
    }
    
}
