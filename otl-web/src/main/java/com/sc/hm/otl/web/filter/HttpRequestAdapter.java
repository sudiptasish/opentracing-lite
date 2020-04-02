/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

import io.opentracing.propagation.TextMapExtract;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This is the http request adapter class that will hold all the header values.
 *
 * @author Sudiptasish Chanda
 */
public class HttpRequestAdapter implements TextMapExtract {
    
    private final Map<String, String> headers = new HashMap<>();
    
    HttpRequestAdapter(HttpServletRequest request) {
        for (Enumeration<String> enm = request.getHeaderNames(); enm.hasMoreElements(); ) {
            String key = enm.nextElement();
            headers.put(key, request.getHeader(key));
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().iterator();
    }
    
}
