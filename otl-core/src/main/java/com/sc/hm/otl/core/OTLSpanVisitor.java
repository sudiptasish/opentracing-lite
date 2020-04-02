/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.Span;

/**
 * Platform specific visitor.
 * 
 * The visitor design pattern is a way of separating an algorithm from an object
 * structure on which it operates. A visitor is primarily called only after a span
 * is finished. A visitor will traverse through the {@link Span} and collect contextual
 * information only to send them to specific destination.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLSpanVisitor {
    
    /**
     * As part of the API call, all the contextual data will be collected from
     * the span and dispatched to specific destination.
     * The destination could be in memory, standard console or file.
     * 
     * @param span
     * @param param 
     */
    void visit(OTLSpan span, Object param);
}
