/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.buff.BufferCache;
import com.sc.hm.otl.buff.LogBuffer;
import com.sc.hm.otl.core.OTLReference;
import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.log.OTLContextLogger;
import com.sc.hm.otl.util.DateUtil;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLSpanVisitorImpl implements OTLSpanVisitor {
    
    private final BufferCache cache;
    private final byte indentation = 2;
    
    private boolean pretty = false;
    
    OTLSpanVisitorImpl() {
        cache = new BufferCache();
        pretty = Boolean.getBoolean("pretty.span");
    }

    @Override
    public void visit(OTLSpan span, Object param) {
        // Will try to generate the final span report in json format.
        boolean reused = true;
        LogBuffer buffer = cache.next();
        
        if (buffer == null) {
            buffer = new LogBuffer();
            reused = false;
        }
        try {
            OTLSpanContext context = (OTLSpanContext)span.context();
            
            buffer.add("{");
            buildContext(context, buffer);
            buildReference(span.references(), buffer);
            
            if (pretty) {
                indent(buffer);
            }
            buffer.add("\"operation\"").add(":").add("\"").add(span.operation()).add("\"").add(",");
            
            buildBaggageItems(context, buffer);
            buildTags(span, buffer);
            
            if (param != null) {
                Object[] params = (Object[])param;
                long timestamp = (Long)params[0];
                String event = (String)params[1];
                Map<String, ?> fields = (Map<String, ?>)params[2];
                
                if (pretty) {
                    indent(buffer);
                }
                buffer.add("\"timestamp\"").add(":").add("\"").add(DateUtil.format(new Date(timestamp / 1000))).add(",");
                if (event != null) {
                    if (pretty) {
                        indent(buffer);
                    }
                    buffer.add("\"event\"").add(":").add("\"").add(event).add("\"").add(",");
                }
                if (fields != null) {
                    buildFromMap("fields", fields, buffer);
                }
            }
            
            if (pretty) {
                indent(buffer);
            }
            buffer.add("\"start\"").add(":").add(span.startTime()).add(",");
            if (pretty) {
                indent(buffer);
            }
            buffer.add("\"end\"").add(":").add(span.endTime());
            if (pretty) {
                buffer.add("\n");
            }
            buffer.add("}");
            
            String spanText = buffer.toString();
            OTLContextLogger.log(spanText);
        }
        finally {
            if (reused) {
                buffer.clear();
                cache.release(buffer);
            }
        }
    }
    
    private void buildContext(OTLSpanContext context, LogBuffer buffer) {
        if (pretty) {
            indent(buffer);
        }
        buffer.add("\"traceId\"").add(":").add("\"").add(context.toTraceId()).add("\"").add(",");
        if (pretty) {
            indent(buffer);
        }
        buffer.add("\"spanId\"").add(":").add("\"").add(context.toSpanId()).add("\"").add(",");
    }

    private void buildReference(List<OTLReference> references, LogBuffer buffer) {
        int size = references.size();
        if (size > 0) {
            if (pretty) {
                indent(buffer);
            }
            buffer.add("\"references\"").add(":").add("[");
            byte gap = (byte)(indentation * 2);
            
            for (byte i = 0; i < size; i ++) {
                buffer.add(" {");
                OTLReference reference = references.get(i);
                if (pretty) {
                    indent(buffer, gap);
                }
                buffer.add("\"type\"").add(":").add("\"").add(reference.type()).add("\"").add(",");
                buffer.add("\"spanId\"").add(":").add("\"").add(reference.context().toSpanId()).add("\"");
                buffer.add("}");
                
                if (i < size - 1) {
                    buffer.add(",");
                }
            }
            if (pretty) {
                indent(buffer);
            }
            buffer.add("]").add(",");
        }
    }
    
    private void buildBaggageItems(OTLSpanContext context, LogBuffer buffer) {
        buildFromMap("baggage", context.getBaggageItems(), buffer);
    }
    
    private void buildTags(OTLSpan span, LogBuffer buffer) {
        buildFromMap("tag", span.tags(), buffer);
    }
    
    private void buildFromMap(String key, Map<String, ?> map, LogBuffer buffer) {
        int size = map.size();
        if (size > 0) {
            byte i = 0;
            if (pretty) {
                indent(buffer);
            }
            buffer.add("\"").add(key).add("\"").add(":").add("{");
            byte gap = (byte)(indentation * 2);

            for (Map.Entry<String, ?> me : map.entrySet()) {
                if (pretty) {
                    indent(buffer, gap);
                }
                buffer.add("\"").add(me.getKey()).add("\"").add(":").add("\"").add(me.getValue() != null ? me.getValue().toString() : "").add("\"");
                i ++;
                if (i < size) {
                    buffer.add(",");
                }
            }
            if (pretty) {
                indent(buffer);
            }
            buffer.add("}").add(",");
        }
    }
    
    private void indent(LogBuffer buffer) {
        indent(buffer, indentation);
    }
    
    private void indent(LogBuffer buffer, byte gap) {
        buffer.add("\n");
        for (byte i = 0; i < gap; i ++) {
            buffer.add(" ");
        }
    }
}
