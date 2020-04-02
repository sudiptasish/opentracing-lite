/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.agent.provider;

import io.opentracing.Tracer;

/**
 *
 * @author Sudiptasish Chanda
 */
public class NoOpTracerProvider extends Provider {
    
    NoOpTracerProvider() {}

    @Override
    public Tracer createTracer() {
        return null;
    }
    
}
