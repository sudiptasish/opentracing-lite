/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

/**
 * Platform provided extractor.
 * 
 * Extractor is responsible for extracting the essential attribute values from the
 * carrier only to initialize a {@link OTLSpanContext}. Extractor will be invoked
 * whenever there is an incoming request or a call from an upstream system. If
 * the client/upstream system provides the tracing information as part of the call,
 * then all such details will be extracted from the incoming request to help build
 * the {@link OTLSpanContext}. If no tracing information is provided in the request,
 * the carrier will be empty, therefore no span context will be created.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLExtractor<C> {
    
    /**
     * Method to extract the tracing information from the carrier and build the
     * span context. The carrier is build out if incoming request taking all the
     * necessary request headers in place. If the carrier is found to be empty
     * then the returned value will be null.
     * 
     * The following information is extracted as part of the extract API.
     * 1. TraceId
     * 2. SpanId
     * 3. Sampled
     * 4. Baggage Items
     * 
     * @param carrier
     * @return OTLSpanContext
     */
    OTLSpanContext extract(C carrier);
}
