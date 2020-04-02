/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.core.OTLSyncScopeManager;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import com.sc.hm.otl.core.OTLExtractor;
import com.sc.hm.otl.core.OTLInjector;
import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.VisitorFactory;

/**
 * Concrete implementation of the {@link OTLTracer}.
 *
 * @author Sudiptasish Chanda
 */
public class OTLTracerImpl implements OTLTracer {
    
    private final OTLSyncScopeManager scopeManager;
    private final CarrierRegistry registry;
    private final OTLSpanVisitor visitor;
    
    OTLTracerImpl() {
        scopeManager = new OTLSyncScopeManagerImpl();
        registry = new CarrierRegistry();
        visitor = VisitorFactory.getFactory().getVisitor();
    }

    @Override
    public ScopeManager scopeManager() {
        return scopeManager;
    }

    @Override
    public Span activeSpan() {
        return scopeManager.activeSpan();
    }

    @Override
    public Scope activateSpan(Span span) {
        return scopeManager.activate(span);
    }

    @Override
    public SpanBuilder buildSpan(String operation) {
        return new OTLTracer.SpanBuilder(this, operation);
    }

    @Override
    public <C> void inject(SpanContext sc, Format<C> format, C c) {
        OTLInjector<C> injector = registry.getInjector(format);
        injector.inject((OTLSpanContext)sc, c);
    }

    @Override
    public <C> OTLSpanContext extract(Format<C> format, C c) {
        OTLExtractor<C> extractor = registry.getExtractor(format);
        return extractor.extract(c);
    }

    @Override
    public void close() {
        // Do Nothing.
    }

    @Override
    public OTLSpanVisitor visitor() {
        return visitor;
    }

    @Override
    public SpanBuilder resumeSpan(SpanContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
