/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.metrics;

import com.sc.hm.otl.core.metric.EventType;
import com.sc.hm.otl.core.metric.MetricAdapter;
import com.sc.hm.otl.metrics.server.PlatformMBeanServer;
import com.sc.hm.otl.metrics.stats.StatsContainer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Platform provided metric adapter.
 *
 * @author Sudiptasish Chanda
 */
public class OTLMetricAdapter implements MetricAdapter  {
    
    private final StatsContainer container = StatsContainer.get();
    
    private final PlatformMBeanServer server = PlatformMBeanServer.get();
    
    private final ObjectName SPAN_MBEAN;
    
    OTLMetricAdapter() {
        try {
            SPAN_MBEAN = new ObjectName(PlatformMBeanServer.OTL_DOMAIN + ":type=core,name=span");
        }
        catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateTracerMetric(EventType type, Object arg) {
        // TODO
    }

    @Override
    public void updateSpanMetric(EventType type, Object arg) {
        if (EventType.SPAN_CREATED == type) {
            container.updateNewSpanStatistics((Long)arg);
        }
        else if (EventType.SPAN_FINISHED == type) {
            Object[] args = (Object[])arg;
            container.updateCompletedSpanStatistics((String)args[0], (Long)args[1]);
        }
    }

    @Override
    public void updateScopeMetric(EventType type, Object arg) {
        if (EventType.SCOPE_ATIVATED == type) {
            container.updateActivatedScopeStatistics();
        }
        else if (EventType.SCOPE_CLOSED == type) {
            container.updateClosedScopeStatistics();
        }
    }
    
}
