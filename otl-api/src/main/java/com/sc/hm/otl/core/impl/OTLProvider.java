/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
