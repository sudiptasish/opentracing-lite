/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import io.opentracing.propagation.TextMapInject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.MultivaluedMap;

/**
 * This is the container request adapter class that will hold all the header values
 * of the client request.
 *
 * @author Sudiptasish Chanda
 */
public class ClientRequestAdapter implements TextMapInject {
    
    private final MultivaluedMap<String, Object> headers;

    ClientRequestAdapter(ClientRequestContext requestCtx) {
        this.headers = requestCtx.getHeaders();
    }

    @Override
    public void put(String key, String value) {
        headers.add(key, value);
    }
}
