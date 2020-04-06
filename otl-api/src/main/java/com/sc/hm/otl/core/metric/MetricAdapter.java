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
