/*
 *     Copyright 2020 Opentracing-LiTE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opns.otl.metrics;

import io.opns.otl.core.metric.EventType;
import io.opns.otl.core.metric.MetricAdapter;
import io.opns.otl.metrics.server.PlatformMBeanServer;
import io.opns.otl.metrics.stats.StatsContainer;
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
