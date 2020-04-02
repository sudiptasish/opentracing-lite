/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;

/**
 * GlobalTracer is a wrapper class that encapsulates the provider specific tracer.
 * It acts as a delegate that transfer all the calls to underlying tracer.
 * It is the only way to get a handle to platform tracer.
 * 
 * <p>
 * It is important to note that, one can have only a single instance of {@link OTLTracer}
 * in a JVM. So we need a holder to keep the tracer instance. The instance of
 * {@link OTLTracer} is created during initialization phase, and stored inside the
 * GlobalTracer.
 * </p>
 * 
 * @author Sudiptasish Chanda
 */
public final class OTLGlobalTracer implements Tracer {
    
    private static final OTLGlobalTracer INSTANCE = new OTLGlobalTracer();
    
    private final Tracer tracer;
    
    private OTLGlobalTracer() {
        this.tracer = new OTLTracerImpl();
    }
    
    static OTLGlobalTracer get() {
        return INSTANCE;
    }

    @Override
    public ScopeManager scopeManager() {
        return tracer.scopeManager();
    }

    @Override
    public Span activeSpan() {
        return tracer.activeSpan();
    }

    @Override
    public Scope activateSpan(Span span) {
        return tracer.activateSpan(span);
    }

    @Override
    public SpanBuilder buildSpan(String operationName) {
        return tracer.buildSpan(operationName);
    }

    @Override
    public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
        tracer.inject(spanContext, format, carrier);
    }

    @Override
    public <C> SpanContext extract(Format<C> format, C carrier) {
        return tracer.extract(format, carrier);
    }

    @Override
    public void close() {
        tracer.close();
    }
    
}
