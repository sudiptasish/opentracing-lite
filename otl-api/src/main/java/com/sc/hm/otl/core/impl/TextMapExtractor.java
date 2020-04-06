/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.util.OTLConstants;
import io.opentracing.propagation.TextMapExtract;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.sc.hm.otl.core.OTLExtractor;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TextMapExtractor implements OTLExtractor<TextMapExtract> {
    
    TextMapExtractor() {}

    @Override
    public OTLSpanContext extract(TextMapExtract carrier) {
        String key = null;
        String value = null;
        
        String traceId = null;
        String spanId = null;
        Map<String, String> baggages = new HashMap<>();
        Integer sampled = 0;
        
        // It will iterate through the carrier to look for the special headers,
        // and if found , build the span context.
        for (Iterator<Map.Entry<String,String>> itr = carrier.iterator(); itr.hasNext(); ) {
            Map.Entry<String,String> me = itr.next();
            if ((key = me.getKey()) == null || (value = me.getValue()) == null) {
                continue;
            }
            if (OTLConstants.TRACE_ID_HEADER.equalsIgnoreCase(key)) {
                traceId = value;
            }
            else if (OTLConstants.SPAN_ID_HEADER.equalsIgnoreCase(key)) {
                spanId = value;
            }
            else if (OTLConstants.SAMPLED_HEADER.equalsIgnoreCase(key)) {
                sampled = Integer.parseInt(value);
            }
            else if (OTLConstants.BAGGAGE_ITEMS_HEADER.equalsIgnoreCase(key)) {
                // Per RFC 2616, multiple baggage items (key=value) must be specified
                // in a comma separated manner.
                String[] s = value.split(",");
                for (byte i = 0; i < s.length; i ++) {
                    String[] items = s[i].split(OTLConstants.BAGGAGE_ITEM_SEPARATOR);
                    baggages.put(items[0].trim(), items[1].trim());
                }
            }
            else {
                int index = key.indexOf(OTLConstants.BAGGAGE_PREFIX_HEADER, 0);
                if (index == 0) {
                    baggages.put(key.substring(OTLConstants.BAGGAGE_PREFIX_HEADER.length()), value);
                }
            }
        }
        if (traceId != null) {
            return new OTLSpanContextImpl(traceId, spanId, baggages, sampled);
        }
        return null;
    }
    
}
