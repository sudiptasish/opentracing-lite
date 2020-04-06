/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLReference;
import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.Visitable;
import com.sc.hm.otl.core.metric.EventSource;
import com.sc.hm.otl.core.metric.EventType;
import com.sc.hm.otl.core.metric.MetricExtension;
import com.sc.hm.otl.util.TracingUtility;
import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.tag.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLSpanImpl implements OTLSpan, Visitable {
    
    private final OTLTracer tracer;
    
    // Every span is identified by a meaningful operation name.
    private final String operation;
    
    // The span context.
    // It encapsulates the traceId, spanId and parent spanId (if any), along with
    // the baggage items.
    private final OTLSpanContext context;
    private final List<OTLReference> references;
    private final Map<String, Object> tags;
    
    private final Object callback;
    private final boolean ignoreActive;
    private final long startMicros;
    private long endMicros = -1L;
    
    OTLSpanImpl(OTLTracer tracer
        , String operation
        , List<OTLReference> references
        , Map<String, Object> tags
        , Object callback
        , boolean ignoreActive
        , long startMicros) {
        
        this.tracer = Objects.requireNonNull(tracer, "Tracer should be non-null");
        this.operation = Objects.requireNonNull(operation, "Operation name should be non-null");
        this.references = references == null ? new ArrayList<>(1) : references;
        
        // Out of all, only traceId, spanId, sampled and baggage item should constitute
        // a SpanContext. Therefore initialize the context.
        if (this.references.isEmpty()) {
            this.context = new OTLSpanContextImpl(TracingUtility.newTraceId()
                , TracingUtility.newSpanId()
                , null
                , null
                , 0);
        }
        else {
            // Extract the span with refType as CHILD_OF
            OTLSpanContext tmp = null;
            for (OTLReference reference : this.references) {
                if (reference.type().equals(References.CHILD_OF)) {
                    tmp = new OTLSpanContextImpl(reference.context().toTraceId()
                        , TracingUtility.newSpanId()
                        , reference.context().toSpanId()
                        , reference.context().getBaggageItems()
                        , reference.context().sampled());
                    
                    break;
                }
            }
            // If no span context is created, that means no CHILD_OF relationship
            // actually exists. In that case, there would be exactly one reference
            // element of type FOLLOWS_FROM.
            // Consider that as a parent, but remember the relationship would still
            // be FOLLOWS_FROM.
            if (tmp == null) {
                OTLReference reference = this.references.get(0);
                tmp = new OTLSpanContextImpl(reference.context().toTraceId()
                        , TracingUtility.newSpanId()
                        , reference.context().toSpanId()
                        , reference.context().getBaggageItems()
                        , reference.context().sampled());
            }
            this.context = Objects.requireNonNull(tmp, "Context should be non-null");
        }
        this.tags = tags != null ? tags : new HashMap<>();
        this.callback = callback;
        this.ignoreActive = ignoreActive;
        this.startMicros = startMicros;
    }

    @Override
    public OTLSpanContext context() {
        return context;
    }

    @Override
    public List<OTLReference> references() {
        return references;
    }

    @Override
    public String parentSpanId() {
        String parentId = "";
        for (OTLReference reference : references) {
            if (References.CHILD_OF.equals(reference.type())) {
                parentId = reference.context().toSpanId();
                break;
            }
        }
        if (parentId.length() == 0 && references.size() == 1) {
            parentId = references.get(0).context().toSpanId();
        }
        return parentId;
    }

    @Override
    public boolean ignoreActive() {
        return this.ignoreActive;
    }

    @Override
    public Span setTag(String key, String value) {
        tags.put(key, value);
        return this;
    }

    @Override
    public Span setTag(String key, boolean value) {
        tags.put(key, value);
        return this;
    }

    @Override
    public Span setTag(String key, Number value) {
        tags.put(key, value);
        return this;
    }

    @Override
    public <T> Span setTag(Tag<T> tag, T value) {
        tags.put(tag.getKey(), value);
        return this;
    }

    @Override
    public Span log(Map<String, ?> fields) {
        return log(System.nanoTime() / 1000, fields);
    }

    @Override
    public Span log(long timestampMicroseconds, Map<String, ?> fields) {
        internalLog(timestampMicroseconds, null, fields);
        return this;
    }

    @Override
    public Span log(String event) {
        return log(System.nanoTime() / 1000, event);
    }

    @Override
    public Span log(long timestampMicroseconds, String event) {
        internalLog(timestampMicroseconds, event, null);
        return this;
    }
    
    private void internalLog(long micros, String event, Map<String, ?> fields) {
        tracer.visitor().visit(this, new Object[] {micros, event, fields});
    }

    @Override
    public Span setBaggageItem(String key, String value) {
        context.addBaggageItem(key, value);
        return this;
    }

    @Override
    public String getBaggageItem(String key) {
        return context.getBaggageItem(key);
    }

    @Override
    public Span setOperationName(String operationName) {
        throw new IllegalStateException(
            String.format("Can not change the operation name [%s] to [%s]"
                , this.operation
                , operationName));
    }

    @Override
    public Map<String, Object> tags() {
        return tags;
    }

    @Override
    public void finish() {
        finish(System.nanoTime() / 1000);
    }

    @Override
    public void finish(long finishMicros) {
        endMicros = finishMicros;
        accept(tracer.visitor());
        
        // Fire the span finish event.
        MetricExtension.fireEvent(EventSource.SPAN
            , EventType.SPAN_FINISHED
            , new Object[] {context.toSpanId(), (endMicros - startMicros)});
    }

    @Override
    public String operation() {
        return operation;
    }

    @Override
    public long startTime() {
        return startMicros;
    }

    @Override
    public long endTime() {
        return endMicros;
    }

    @Override
    public void accept(OTLSpanVisitor visitor) {
        visitor.visit(this, null);
    }

    @Override
    public String toString() {
        return "Span Context: " + context.toString()
            + ". Operation: " + operation
            + ". Tags: " + tags
            + ". Start: " + startMicros
            + ". End: " + endMicros;
    }

    @Override
    public Object callback() {
        return this.callback;
    }
}
