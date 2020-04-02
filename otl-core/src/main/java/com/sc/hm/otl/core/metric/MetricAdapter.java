/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.metric;

/**
 *
 * An adapter interface in order to allow two unrelated/uncommon interfaces to work together.
 * In other words, it enable the framework to leverage the underlying framework's 
 * certain capabilities, here passing captured metric data, thus making two incompatible
 * interfaces compatible without changing their existing code.
 * 
 * The platform provider must define their own Adapter in order to propagate the
 * call to underlying layer.
 *
 * @author Sudiptasish Chanda
 */
public interface MetricAdapter {
    
    /**
     * Update the underlying tracer metric.
     * 
     * @param type
     * @param arg
     */
    void updateTracerMetric(EventType type, Object arg);
    
    /**
     * Update the underlying span metric.
     * 
     * @param type
     * @param arg
     */
    void updateSpanMetric(EventType type, Object arg);
    
    /**
     * Update the underlying scope metric.
     * 
     * @param type
     * @param arg
     */
    void updateScopeMetric(EventType type, Object arg);
}
