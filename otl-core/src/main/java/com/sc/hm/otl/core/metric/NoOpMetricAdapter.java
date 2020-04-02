/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.metric;

/**
 * A simple adapter to capture and store the metric value locally
 * 
 * @author Sudiptasish Chanda
 */
public class NoOpMetricAdapter implements MetricAdapter {

    @Override
    public void updateTracerMetric(EventType type, Object arg) {
        // NO-OP
    }

    @Override
    public void updateSpanMetric(EventType type, Object arg) {
        // NO-OP
    }

    @Override
    public void updateScopeMetric(EventType type, Object arg) {
        // NO-OP
    }

    
}
