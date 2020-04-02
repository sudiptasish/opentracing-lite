/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import com.sc.hm.otl.core.impl.OTLVisitorFactory;
import io.opentracing.Tracer;

/**
 * Factory class for instantiating the visitor.
 *
 * @author Sudiptasish Chanda
 */
public abstract class VisitorFactory {
    
    /**
     * Return the platfor visitor factory.
     * @return VisitorFactory
     */
    public static VisitorFactory getFactory() {
        return new OTLVisitorFactory();
    }
    
    /**
     * API to create a new visitor.
     * A visitor object is attached to a {@link Tracer}. Therefore it's lifecycle
     * is managed by the tracer itself. It will stay there as long as the Tracer
     * object is active.
     * 
     * Note that, {@link OTLSpanVisitor} is stateless, that means, it does not
     * maintain any state, which is why it is thread safe.
     * 
     * @return 
     */
    public abstract OTLSpanVisitor getVisitor();
}
