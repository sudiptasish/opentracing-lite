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
package io.opns.otl.metrics.server;

import io.opns.otl.metrics.mbean.LoggingMXBean;
import io.opns.otl.metrics.mbean.LoggingMXBeanImpl;
import io.opns.otl.metrics.mbean.ScopeMXBean;
import io.opns.otl.metrics.mbean.ScopeMXBeanImpl;
import io.opns.otl.metrics.mbean.SpanMXBean;
import io.opns.otl.metrics.mbean.SpanMXBeanImpl;
import io.opns.otl.metrics.mbean.TracerMXBean;
import io.opns.otl.metrics.mbean.TracerMXBeanImpl;
import io.opns.otl.metrics.stats.StatsContainer;

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
