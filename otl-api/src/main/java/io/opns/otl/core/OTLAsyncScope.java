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
package io.opns.otl.core;

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
     * @param span  The span to be added to this async scope.
     */
    void add(Span span);
    
    /**
     * Return the currently active span.
     * It basically returns the most recent span created.
     * 
     * @return Span The currently active span,means teh topo most element of
     *              the stack.
     */
    Span active();
    
    /**
     * Remove and return the current span from this scope.
     * @return Span Return the currently active span.
     */
    Span removeCurrent();
    
    /**
     * Return the total number of un-finished spans present in this scope.
     * @return int  Total number of spans present in this async scope.
     */
    int spanCount();
}
