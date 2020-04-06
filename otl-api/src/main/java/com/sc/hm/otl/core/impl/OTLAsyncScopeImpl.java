/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLAsyncScope;
import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.core.ctx.OTLContext;
import io.opentracing.Span;
import java.util.Collection;
import java.util.Stack;

/**
 * Implementation of {@link OTLAsyncScope}.
 *
 * @author Sudiptasish Chanda
 */
public class OTLAsyncScopeImpl implements OTLAsyncScope {
    
    // A stack to keep all the spans generated in an async scope.
    private final Stack<Span> bucket = new Stack<>();

    public OTLAsyncScopeImpl() {
    }

    @Override
    public void add(Span span) {
        bucket.push(span);
        //setContextInfo();
    }

    @Override
    public Span active() {
        return bucket.peek();
    }

    @Override
    public Span removeCurrent() {
        return bucket.pop();
    }

    @Override
    public void close() {
        bucket.clear();
        //resetContextInfo();
    }

    @Override
    public int spanCount() {
        return bucket.size();
    }

    @Override
    public String toString() {
        return "Un-finished span(s): " + bucket.toString();
    }
    
    /**
     * If system detects any underlying logging framework, then it will be set
     * in the thread context.
     *
     * Note: Only the following context parameter(s) will be set.
     * 1. TraceId      - %X{t}
     * 2. SpanId       - %X{s}
     * 3. Baggage      - %X{b}
     * 4. Operation    - %X{o}
     * 5. ParentSpanId - %X{p}
     */
    private void setContextInfo() {
        OTLSpan span = (OTLSpan)bucket.peek();
        OTLSpanContext context = (OTLSpanContext)span.context();
        
        Collection<String> baggageValues = context.getBaggageItems().values();
        
        OTLContext.put("trc", context.toTraceId());
        OTLContext.put("spn", context.toSpanId());
        OTLContext.put("bgi", String.join(" ", baggageValues));
        OTLContext.put("ops", span.operation());
        OTLContext.put("pspn", span.parentSpanId());
    }
    
    /**
     * Reset the contextual data that were originally set.
     */
    private void resetContextInfo() {
        OTLContext.remove("trc");
        OTLContext.remove("spn");
        OTLContext.remove("bgi");
        OTLContext.remove("ops");
        OTLContext.remove("pspn");
    }
}
