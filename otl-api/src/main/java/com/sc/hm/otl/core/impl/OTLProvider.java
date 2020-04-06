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
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.agent.provider.Provider;
import io.opentracing.Tracer;
import java.util.ServiceLoader;

/**
 * Provider class for opentracing-LiTe.
 * 
 * OTL provider is responsible for create and returning an instance of a tracer
 * that is compliant with the spec as demonstrated and laid down by io.opentracing
 * community.
 *
 * There will be a single instance of type {@link OTLTracer} per jvm. The provider
 * is loaded into the memory via {@link ServiceLoader} API, which can thereafter
 * be used to obtain a valid tracer instance.
 * 
 * The provider does not keep a local copy of the tracer. The returned tracer
 * object will be directly registered with {@link GlobalTracer} object to make
 * is globally available. A second invocation of {@link #createTracer() } method
 * will throw {@link IllegalStateException}.
 * 
 * @author Sudiptasish Chanda
 */
public class OTLProvider extends Provider {
    
    private static volatile boolean initialized = false;
    
    public OTLProvider() {}
    
    @Override
    public Tracer createTracer() {
        synchronized (OTLProvider.class) {
            if (initialized) {
                throw new IllegalStateException("Provider is already initialized. To obtain"
                    + " the platform tracer use GlobalTracer.get()");
            }
            initialized = true;
            return new OTLTracerImpl();
        }
    }
}
