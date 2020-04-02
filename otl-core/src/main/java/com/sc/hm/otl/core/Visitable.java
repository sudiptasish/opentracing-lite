/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

/**
 * Represents an object that can potentially be visited by a visitor.
 *
 * @author Sudiptasish Chanda
 */
public interface Visitable {
    
    /**
     * API to grant the permission to a visitor.
     * 
     * @param visitor 
     */
    void accept(OTLSpanVisitor visitor);
}
