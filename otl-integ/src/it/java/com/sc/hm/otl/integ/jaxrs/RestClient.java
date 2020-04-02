/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.jaxrs;

import com.sc.hm.otl.jaxrs.filter.OTLClientRequestFilter;
import com.sc.hm.otl.jaxrs.filter.OTLClientResponseFilter;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 *
 * @author Sudiptasish Chanda
 */
public class RestClient {
    
    private static final Client CLIENT;
    
    static {
        CLIENT = ClientBuilder
            .newBuilder()
            .register(new OTLClientRequestFilter())
            .register(new OTLClientResponseFilter())
            .build();
    }
    
    public static Client restClient() {
        return CLIENT;
    }
}
