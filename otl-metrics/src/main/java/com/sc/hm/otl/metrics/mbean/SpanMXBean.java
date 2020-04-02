/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.metrics.mbean;

/**
 *
 * @author Sudiptasish Chanda
 */
public interface SpanMXBean {
    
    /**
     * Return the total number of spans created so far.
     * This includes the span that were finished as well as active.
     * 
     * @return Long
     */
    Long getTotalSpans();
    
    /**
     * Get the max time spent to create a span.
     * @return Long
     */
    Long getMaxSpanCreationTime();
    
    /**
     * Get the average span creation time.
     * @return Long
     */
    Long getAvgSpanCreationTime();
    
    /**
     * Return the longest duration that a span took to complete.
     * This API will consider only those spans, for which the {@link Span#finish}
     * method had already been invoked.
     * 
     * @return Long
     */
    Long getMaxSpanDuration();
    
    /**
     * Get the the average duration of a span.
     * @return Long
     */
    Long getAvgSpanDuration();
    
    /**
     * Return the id of the span that took the maximum time to finish.
     * @return String
     */
    String getMaxDurationSpanId();
    
    /**
     * Return the totalnumber of spans that were closed.
     * @return Long
     */
    Long getTotalFinishedSpans();
}
