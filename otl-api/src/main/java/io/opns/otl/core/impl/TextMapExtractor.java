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
package io.opns.otl.core.impl;

import io.opns.otl.core.OTLSpanContext;
import io.opns.otl.util.OTLConstants;
import io.opentracing.propagation.TextMapExtract;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import io.opns.otl.core.OTLExtractor;

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
