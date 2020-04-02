/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.metric;

/**
 *
 * @author Sudiptasish Chanda
 */
public enum EventType {
    
    SPAN_CREATED ("Indicates the creation of a Span")
    , SPAN_FINISHED ("Indicates that a span has just finished")
    , SCOPE_ATIVATED ("Indicates that a span scope has just been activated")
    , SCOPE_CLOSED ("Indicates that a span scope has just been closed");
    
    final String desc;
    
    EventType(String desc) {
        this.desc = desc;
    }
}
