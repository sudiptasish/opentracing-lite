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
     * @param carrier   The carrier object where the current contextual info
     *                  will be injected.
     */
    void inject(C carrier);
    
    /**
     * Take the tracing details from the spancontext provided and push them into
     * the carrier, which is tied to the outbound request.
     * 
     * @param context   Current span context.
     * @param carrier   The carrier object where the current contextual info
     *                  will be injected.
     */
    void inject(OTLSpanContext context, C carrier);
}
