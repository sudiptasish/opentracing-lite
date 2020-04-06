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

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.core.OTLSyncScope;
import com.sc.hm.otl.core.ctx.OTLContext;
import com.sc.hm.otl.core.metric.EventSource;
import com.sc.hm.otl.core.metric.EventType;
import com.sc.hm.otl.core.metric.MetricExtension;
import io.opentracing.Span;
import java.util.Collection;
import java.util.Objects;

/**
 * Implementation of a sync scope.
 *
 * @author Sudiptasish Chanda
 */
public class OTLSyncScopeImpl implements OTLSyncScope {
    
    private final OTLSyncScopeManagerImpl scopeManager;
    private final OTLSpan span;
    private final OTLSyncScope parent;
    
    OTLSyncScopeImpl(OTLSyncScopeManagerImpl scopeManager
        , OTLSpan span
        , OTLSyncScope parent) {
        
        this.scopeManager = Objects.requireNonNull(scopeManager, "Scope Manager must be non-null");
        this.span = Objects.requireNonNull(span, "Span must be non-null");
        this.parent = parent;
        
        // Now set the contextual data.
        scopeManager.tlScope.set(this);
        setContextInfo();
        
        // Fire the scope activation event.
        MetricExtension.fireEvent(EventSource.SCOPE
            , EventType.SCOPE_ATIVATED
            , null);
    }

    @Override
    public void close() {
        if (span == null) {
            throw new IllegalStateException("Closing an invalid scope");
        }
        resetContextInfo();
        scopeManager.tlScope.remove();
        
        if (parent != null) {
            scopeManager.tlScope.set(parent);
            setContextInfo();
        }
        // Fire the scope close event.
        MetricExtension.fireEvent(EventSource.SCOPE
            , EventType.SCOPE_CLOSED
            , null);
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
        OTLSyncScope scope = scopeManager.tlScope.get();
        OTLSpan span = (OTLSpan)scope.span();
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

    @Override
    public Span span() {
        return this.span;
    }
}
