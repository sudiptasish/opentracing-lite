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
package io.opns.otl.core.impl;

import io.opns.otl.core.OTLAsyncScopeManager;
import io.opns.otl.core.OTLSpanContext;
import io.opns.otl.core.OTLSyncScopeManager;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import io.opns.otl.core.OTLExtractor;
import io.opns.otl.core.OTLInjector;
import io.opns.otl.core.OTLSpanVisitor;
import io.opns.otl.core.VisitorFactory;

/**
 * Concrete implementation of the {@link OTLTracer}.
 *
 * @author Sudiptasish Chanda
 */
public class OTLTracerImpl implements OTLTracer {
    
    private final OTLSyncScopeManager scopeManager;
    private final OTLAsyncScopeManager asyncScopeManager;
    private final CarrierRegistry registry;
    private final OTLSpanVisitor visitor;
    
    OTLTracerImpl() {
        scopeManager = new OTLSyncScopeManagerImpl();
        asyncScopeManager = new OTLAsyncScopeManagerImpl();
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
    public Scope activateAsync(Span span) {
        return asyncScopeManager.activate(span);
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
