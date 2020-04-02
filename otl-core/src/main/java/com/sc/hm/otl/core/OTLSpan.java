/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.Span;
import java.util.List;
import java.util.Map;

/**
 * OTL span.
 * It extends the characteristic of opentracing {@link span} and provides some 
 * additional behavior. 
 *
 * @author Sudiptasish Chanda
 */
public interface OTLSpan extends Span {
    
    /**
     * Return the operation name.
     * It is advisable to provide a unique name to the span. 
     * 
     * @return String
     */
    String operation();
    
    /**
     * Return the references.
     * @return List
     */
    List<OTLReference> references();
    
    /**
     * Convenient method to get the parent span id.
     * @return String
     */
    String parentSpanId();
    
    /**
     * Indicate whether the span activation to be ignored.
     * @return boolean
     */
    boolean ignoreActive();
    
    /**
     * Return the tags.
     * @return Map
     */
    Map<String, Object> tags();
    
    /**
     * Return the start time of the span in microseconds.
     * @return long
     */
    long startTime();
    
    /**
     * Return the end time of the span in microseconds.
     * @return long
     */
    long endTime();
}
