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
package io.opns.otl.agent.provider;

import io.opentracing.Tracer;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Service Provider for Tracer.
 *
 * @author Sudiptasish Chanda
 */
public abstract class Provider {
 
    /**
     * Creates a new provider object.
     * 
     * <p>
     * The algorithm used to locate the provider subclass to use consists
     * of the following steps:
     * 
     * <ul>
     * <li>
     *   If a resource with the name of
     *   <code>META-INF/services/io.opns.otl.agent.provier.Provider</code>
     *   exists, then its first line, if present, is used as the UTF-8 encoded
     *   name of the implementation class.
     * </li>
     * <li>
     *   If a system property with the name <code>io.opns.otl.agent.provier.Provider</code>
     *   is defined, then its value is used as the name of the implementation class.
     * </li>
     * <li>
     *   Finally, a default implementation class name is used.
     * </li>
     * </ul>
     *
     * @return Provider
     */
    public static Provider provider() {
        return provider(false);
    }
    
    /**
     * Use java's service loader capability to load the available provider(s).
     * If multiple providers were found, then take the first one present in the
     * classpath.
     * 
     * @param refresh   Specify whether a refresh is required.
     *                  If true, then the service loader will try loading the
     *                  the available provider factories again.
     * @return Provider
     */
    public static Provider provider(boolean refresh) {
        ServiceLoader<Provider> loader = ServiceLoader.load(Provider.class);
        if (refresh) {
            loader.reload();
        }
        List<Provider> providers = new ArrayList<>(1);
        for (Provider provider : loader) {
            providers.add(provider);
        }
        if (!providers.isEmpty()) {
            return providers.get(0);
        }
        return new NoOpTracerProvider();
    }
    
    /**
     * API to create the platform specific {@link Tracer}.
     * @return Tracer
     */
    public abstract Tracer createTracer();
}
