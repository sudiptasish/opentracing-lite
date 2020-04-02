/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.VisitorFactory;
import com.sc.hm.otl.util.ObjectCreator;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLVisitorFactory extends VisitorFactory {
    
    public OTLVisitorFactory() {
        super();
    }
    
    @Override
    public OTLSpanVisitor getVisitor() {
        OTLSpanVisitor visitor;
        
        String visitorClass = System.getProperty("span.visitor");
        if (visitorClass != null && (visitorClass = visitorClass.trim()).length() > 0) {
            visitor = ObjectCreator.create(visitorClass);
        }
        else {
            visitor = new OTLSpanVisitorImpl();
        }
        return visitor;
    }
}
