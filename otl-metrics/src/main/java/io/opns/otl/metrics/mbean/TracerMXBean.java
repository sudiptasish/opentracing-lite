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
package io.opns.otl.metrics.mbean;

/**
 * Interface for tracer mbean.
 *
 * @author Sudiptasish Chanda
 */
public interface TracerMXBean {
    
    /**
     * Return the scope manager name.
     * @return String
     */
    String getScopeManagerName();
    
    /**
     * Return the name of the propagation registry.
     * @return String
     */
    String getRegistryName();
    
    /**
     * Get the span visitor name.
     * @return String
     */
    String getSpanVisitor();
}
