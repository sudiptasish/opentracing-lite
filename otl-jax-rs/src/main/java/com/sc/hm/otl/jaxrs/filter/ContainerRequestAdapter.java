/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import io.opentracing.propagation.TextMapExtract;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * This is the container request adapter class that will hold all the header values.
 *
 * @author Sudiptasish Chanda
 */
public class ContainerRequestAdapter implements TextMapExtract {
    
    private final Map<String, String> headers = new HashMap<>();

    ContainerRequestAdapter(ContainerRequestContext requestCtx) {
        List<String> values = null;
        
        for (Map.Entry<String, List<String>> me : requestCtx.getHeaders().entrySet()) {
            values = me.getValue();
            if (values.size() > 1) {
                headers.put(me.getKey(), String.join(",", values.toArray(new String[values.size()])));
            }
            else {
                headers.put(me.getKey(), values.get(0));
            }
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().iterator();
    }
}
