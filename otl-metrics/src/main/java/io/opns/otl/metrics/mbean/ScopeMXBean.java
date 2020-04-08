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
 *
 * @author Sudiptasish Chanda
 */
public interface ScopeMXBean {
    
    /**
     * Return the total number of scope activated.
     * @return Long
     */
    Long getTotalScopeActivated();
    
    /**
     * Return the total number of scope closed.
     * @return Long
     */
    Long getTotalScopeClosed();
}
