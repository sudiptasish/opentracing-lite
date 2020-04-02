/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.Span;

/**
 * Injector will be invoked whenever there is an outboud request.
 * The framework will call the injector to inject the tracing information in the
 * outbound request in order to continue the tracing flow. In order to inject
 * the tracing details, it expects an active {@link Span} to be present in the
 * current thread context. If no span is present or active at that time, no information
 * will be passed over the wire.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLInjector<C> {
    
    /**
     * It will call {@link #inject(OTLSpanContext, Object) } after taking the 
     * active span from the current thread context.
     * 
     * @param carrier 
     */
    void inject(C carrier);
    
    /**
     * Take the tracing details from the spancontext provided and push them into
     * the carrier, which is tied to the outbound request.
     * 
     * @param context
     * @param carrier 
     */
    void inject(OTLSpanContext context, C carrier);
}
