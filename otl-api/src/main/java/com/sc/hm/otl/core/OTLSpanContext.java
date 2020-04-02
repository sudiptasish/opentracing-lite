/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.SpanContext;
import java.util.Map;

/**
 * OTL Span context.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLSpanContext extends SpanContext {
    
    /**
     * Return the parent span id presents in the context.
     * @return String
     */
    //String parentSpanId();
    
    /**
     * Add the baggage item to this span context.
     * 
     * @param key
     * @param value 
     */
    void addBaggageItem(String key, String value);
    
    /**
     * Return the item value corresponding to this item key.
     * @param key
     * @return String
     */
    String getBaggageItem(String key);
    
    /**
     * Get the baggage item map.
     * @return Map
     */
    Map<String, String> getBaggageItems();
    
    /**
     * 
     * @return 
     */
    Integer sampled();
}
