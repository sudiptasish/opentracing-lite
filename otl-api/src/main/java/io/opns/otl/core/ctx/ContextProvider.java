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
package io.opns.otl.core.ctx;

import java.util.ServiceLoader;

/**
 * Interface to represent a third party context provider.
 * Using the java's {@link ServiceLoader} capability the third party context provider
 * is loaded into memory. Which can later be used to instantiate the platform
 * specific {@link ContextAdapter}. If no provider is found, then the default
 * context adapter will be used.
 *
 * @author Sudiptasish Chanda
 */
public interface ContextProvider {
    
    /**
     * Instantiate the platform specific context adapter.
     * @return ContextAdapter
     */
    ContextAdapter create();
}
