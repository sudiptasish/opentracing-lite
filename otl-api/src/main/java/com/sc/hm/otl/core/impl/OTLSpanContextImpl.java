/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLSpanContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Concrete class to represent the span context.
 * A span context is an immutable object, which means once created with the traceId,
 * spanId and parentSpanId, their values cannot be changed.
 *
 * @author Sudiptasish Chanda
 */
public class OTLSpanContextImpl implements OTLSpanContext {
    
    private final String traceId;
    private final String spanId;
    //private final String parentSpanId;
    private final Integer sampled;
    private final Map<String, String> baggageItems;
    
    public OTLSpanContextImpl(String traceId
        , String spanId
        , Map<String, String> baggageItems
        , Integer sampled) {
        
        this(traceId, spanId, null, baggageItems, sampled);
    }

    public OTLSpanContextImpl(String traceId
        , String spanId
        , String parentSpanId
        , Map<String, String> baggageItems
        , Integer sampled) {
        
        this.traceId = Objects.requireNonNull(traceId, "TraceId must be non-null");
        this.spanId = Objects.requireNonNull(spanId, "SpanId must be non-null");
        //this.parentSpanId = parentSpanId != null ? parentSpanId : "";
        this.baggageItems = baggageItems != null ? baggageItems : new HashMap<>(2);
        this.sampled = sampled;
    }

    @Override
    public String toTraceId() {
        return traceId;
    }

    @Override
    public String toSpanId() {
        return spanId;
    }

    //@Override
    //public String parentSpanId() {
    //    return parentSpanId;
    //}

    @Override
    public Iterable<Map.Entry<String, String>> baggageItems() {
        return baggageItems.entrySet();
    }

    @Override
    public void addBaggageItem(String key, String value) {
        baggageItems.put(key, value);
    }

    @Override
    public String getBaggageItem(String key) {
        return baggageItems.get(key);
    }

    @Override
    public Map<String, String> getBaggageItems() {
        return baggageItems;
    }

    @Override
    public Integer sampled() {
        return sampled;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OTLSpanContextImpl) {
            OTLSpanContextImpl other = (OTLSpanContextImpl)obj;
            return traceId.equals(other.traceId) && spanId.equals(other.spanId);
        }
        return false;
    }

    @Override
    public String toString() {
        //return "[TraceId: " + traceId + ". SpanId: " + spanId + ". ParentSpanId: " + parentSpanId + "]";
        return "[TraceId: " + traceId + ". SpanId: " + spanId + ". Sampled: " + sampled + "]";
    }
}
