/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.metrics.server;

import com.sc.hm.otl.metrics.mbean.LoggingMXBean;
import com.sc.hm.otl.metrics.mbean.LoggingMXBeanImpl;
import com.sc.hm.otl.metrics.mbean.ScopeMXBean;
import com.sc.hm.otl.metrics.mbean.ScopeMXBeanImpl;
import com.sc.hm.otl.metrics.mbean.SpanMXBean;
import com.sc.hm.otl.metrics.mbean.SpanMXBeanImpl;
import com.sc.hm.otl.metrics.mbean.TracerMXBean;
import com.sc.hm.otl.metrics.mbean.TracerMXBeanImpl;
import com.sc.hm.otl.metrics.stats.StatsContainer;

/**
 * Bootstrap class for metric.
 *
 * @author Sudiptasish Chanda
 */
public class MetricBootstrap {
    
    public MetricBootstrap() {}
    
    /**
     * Initialize the metric sub-system and register various MBeans.
     */
    public void init() {
        try {
            PlatformMBeanServer server = PlatformMBeanServer.get();

            LoggingMXBean loggingMXBean = new LoggingMXBeanImpl();
            TracerMXBean tracerMXBean = new TracerMXBeanImpl();
            SpanMXBean spanMXBean = new SpanMXBeanImpl();
            ScopeMXBean scopeMXBean = new ScopeMXBeanImpl();
            
            StatsContainer.get().setSpanMXBean((SpanMXBeanImpl)spanMXBean);
            StatsContainer.get().setScopeMXBean((ScopeMXBeanImpl)scopeMXBean);
            
            server.register("ctx", "logging", loggingMXBean);
            server.register("core", "trace", tracerMXBean);
            server.register("core", "span", spanMXBean);
            server.register("core", "scope", scopeMXBean);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
