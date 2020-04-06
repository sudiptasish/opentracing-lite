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
