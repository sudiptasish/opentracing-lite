/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.util;

/**
 *
 * @author Sudiptasish Chanda
 */
public final class OTLConstants {
    
    public static final String TRACE_ID_HEADER = "X-B3-TraceId";
    public static final String SPAN_ID_HEADER = "X-B3-SpanId";
    public static final String BAGGAGE_PREFIX_HEADER = "X-B3-Baggage-";
    public static final String BAGGAGE_ITEMS_HEADER = "X-B3-Baggages";
    public static final String SAMPLED_HEADER = "X-B3-Sampled";
    
    public static final String BAGGAGE_ITEM_SEPARATOR = "=";
    public static final String VERTX_ACTIVE_SPAN = "VAS";
    public static final String VERTX_SCOPE = "VSC";
    
    public static final String DECORATOR = "DECORATOR";
    public static final String SKIP_PATTERN = "SKIP_PATTERN";
    public static final String URL_PATTERN = "URL_PATTERN";
    
    public static final String INCOMPLETE_TAG = "incomplete";
}
