/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.metrics.stats;

import com.sc.hm.otl.metrics.mbean.ScopeMXBeanImpl;
import com.sc.hm.otl.metrics.mbean.SpanMXBeanImpl;

/**
 * A container class to hold the trace/span statistics, before they are actually
 * published to the platform mbean server.
 *
 * @author Sudiptasish Chanda
 */
public final class StatsContainer {
    
    private static final StatsContainer CONTAINER = new StatsContainer();
    
    private SpanMXBeanImpl spanMXBean;
    private ScopeMXBeanImpl scopeMXBean;
    
    private StatsContainer() {}
    
    public static StatsContainer get() {
        return CONTAINER;
    }
    
    public void setSpanMXBean(SpanMXBeanImpl spanMXBean) {
        this.spanMXBean = spanMXBean;
    }
    
    public void setScopeMXBean(ScopeMXBeanImpl scopeMXBean) {
        this.scopeMXBean = scopeMXBean;
    }
    
    public void updateNewSpanStatistics(Long time) {
        spanMXBean.updateSpanCreation(time);
    }
    
    public void updateCompletedSpanStatistics(String spanId, Long time) {
        spanMXBean.updateSpanFinish(spanId, time);
    }
    
    public void updateActivatedScopeStatistics() {
        scopeMXBean.incrementScopeActivated();
    }
    
    public void updateClosedScopeStatistics() {
        scopeMXBean.incrementScopeClosed();
    }
}
