/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.metrics;

import com.sc.hm.otl.core.metric.MetricAdapter;
import com.sc.hm.otl.core.metric.MetricProvider;

/**
 * Platform specific metric provider.
 *
 * @author Sudiptasish Chanda
 */
public class OTLMetricProvider implements MetricProvider {

    @Override
    public MetricAdapter create() {
        return new OTLMetricAdapter();
    }
    
}
