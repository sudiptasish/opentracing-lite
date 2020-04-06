/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * Sync scope is an async extension to the opentracing {@link Scope}.
 * 
 * <p>
 * Async scope does not need a scope manager, because they are never stored in
 * a thread context. A work that is identified by an async scope may start in one
 * thread but finish in a different thread. If the underlying async framework
 * provides some mechanism to pass on some context (not a thread context) data
 * between threads then this scope must be part of that context.
 * 
 * <p>
 * Async scope is created only once and it is per request. At any point of time
 * an async scope may contain multiple spans, out of which one may be active.
 * When the context object will be destroyed, the associated async scope will be 
 * destroyed, too. And all the spans it currently having in store, will be garbage
 * collected.
 * 
 * Following API will start an async scope.
 * <pre>
 * {@code
 *     .....
 * 
 *     OTLTracer tracer = (OTLTracer)GlobalTracer.get();
 *     Span span = tracer.buildSpan(routingCtx.request().method().name())
 *               .asChildOf(context)
 *               .ignoreActiveSpan()
 *               .start();
 * 
 *     Scope asyncScope = tracer.activateAsync(span)
 * }
 * </pre>
 *
 * @author Sudiptasish Chanda
 */
public interface OTLAsyncScope extends Scope {

    /**
     * Add the new span to this scope.
     * An async scope may contain multiple spans. Thew new span will be added top
     * of the internal stack, marking it active.
     * 
     * @param span 
     */
    void add(Span span);
    
    /**
     * Return the currently active span.
     * It basically returns the most recent span created.
     * 
     * @return Span
     */
    Span active();
    
    /**
     * Remove and return the current span from this scope.
     * @return Span
     */
    Span removeCurrent();
    
    /**
     * Return the total number of un-finished spans present in this scope.
     * @return int
     */
    int spanCount();
}
