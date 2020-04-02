/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.util.OTLConstants;
import io.opentracing.propagation.TextMapInject;
import java.util.Map;
import com.sc.hm.otl.core.OTLInjector;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TextMapInjector implements OTLInjector<TextMapInject> {
    
    TextMapInjector() {}

    @Override
    public void inject(TextMapInject carrier) {
        inject(null, carrier);
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
