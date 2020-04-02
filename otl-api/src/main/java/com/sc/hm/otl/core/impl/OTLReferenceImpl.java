/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLReference;
import com.sc.hm.otl.core.OTLSpanContext;

/**
 * Platform provided reference object.
 *
 * @author Sudiptasish Chanda
 */
public class OTLReferenceImpl implements OTLReference {
    
    private final String refType;
    private final OTLSpanContext parent;
    
    public OTLReferenceImpl(String refType, OTLSpanContext parent) {
        this.refType = refType;
        this.parent = parent;
    }

    @Override
    public String type() {
        return refType;
    }

    @Override
    public OTLSpanContext context() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OTLReferenceImpl) {
            OTLReferenceImpl other = (OTLReferenceImpl)obj;
            return refType.equals(other.refType) && parent.equals(other.parent);
        }
        return false;
    }
}
