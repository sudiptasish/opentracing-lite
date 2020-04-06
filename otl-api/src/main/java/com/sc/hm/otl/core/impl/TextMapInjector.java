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

import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.util.OTLConstants;
import io.opentracing.propagation.TextMapInject;
import java.util.Map;
import com.sc.hm.otl.core.OTLInjector;
import io.opentracing.util.GlobalTracer;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TextMapInjector implements OTLInjector<TextMapInject> {
    
    TextMapInjector() {}

    @Override
    public void inject(TextMapInject carrier) {
        OTLSpanContext context = (OTLSpanContext)GlobalTracer.get().activeSpan();
        if (context != null) {
            inject(null, carrier);
        }
    }

    @Override
    public void inject(OTLSpanContext context, TextMapInject carrier) {
        carrier.put(OTLConstants.TRACE_ID_HEADER, context.toTraceId());
        carrier.put(OTLConstants.SPAN_ID_HEADER, context.toSpanId());
        carrier.put(OTLConstants.SAMPLED_HEADER, String.valueOf(context.sampled()));
        
        for (Map.Entry<String, String> me : context.baggageItems()) {
            carrier.put(OTLConstants.BAGGAGE_ITEMS_HEADER + me.getKey(), me.getValue());
        }
    }
}
