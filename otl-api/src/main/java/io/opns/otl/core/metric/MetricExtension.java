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
package io.opns.otl.core.metric;

import io.opentracing.Span;
import io.opentracing.Scope;
import io.opentracing.Tracer;

/**
 * This class hides and serves as a substitute for the underlying metric sub-system
 * implementation of any provider.
 * 
 * <p>
 * If the underlying metric provider the feature of capturing metrics data of the
 * traceing/span, this class, will delegate all the calls to the underlying system's 
 * metric component.
 * It accomplishes the same with the help of {@link MetricAdapter}. If the
 * associated framework provides an adapter then the same will be loaded by the
 * provider library. If no adapter is found, then the default {@link NoOpMetricAdapter}
 * will be used.
 *
 * <p>
 * This class has method pertaining to various metric source. For example, an event
 * may be originated from a {@link Tracer}, or a {@link Span} or a {@link Scope}.
 * We call them as source. Extension class has APIs to capture all sorts of event
 * emitting from different sources.
 * 
 * @author Sudiptasish Chanda
 */
public class MetricExtension {
    
    private static final MetricAdapter METRIC_ADAPTER;
    
    static {
        MetricProvider provider = MetricProviderFactory.getProvider();
        if (provider != null) {
            METRIC_ADAPTER = provider.create();
        }
        else {
            METRIC_ADAPTER = new NoOpMetricAdapter();
        }
    }
    
    /**
     * Send notification indicating an event associated with span has occurred in
     * underlying otl framework.
     * 
     * The event will subsequently be acknowledged by the platform provided
     * {@link MetricAdapter} and appropriate action will be taken.
     * 
     * The implementation of the adapter is left with the 3rd party service provider.
     * There are specific event that are supported today, however, in future more
     * event type may get added.
     * 
     * @param source    The source of the event. E.g., tracer, span, etc.
     * @param type      Event type.
     * @param arg       Optional arguments. 
     */
    public static void fireEvent(EventSource source
        , EventType type
        , Object arg) {
        
        if (EventSource.SPAN == source) {
            METRIC_ADAPTER.updateSpanMetric(type, arg);
        }
        else if (EventSource.SCOPE == source) {
            METRIC_ADAPTER.updateScopeMetric(type, arg);
        }
        else if (EventSource.TRACER == source) {
            METRIC_ADAPTER.updateTracerMetric(type, arg);
        }
    }
}
