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
