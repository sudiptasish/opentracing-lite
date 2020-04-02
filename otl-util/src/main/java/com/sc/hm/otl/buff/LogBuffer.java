/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.buff;

/**
 *
 * @author Sudiptasish Chanda
 */
public class LogBuffer {
    
    private final StringBuilder builder;
    
    public LogBuffer() {
        this(256);
    }
    
    public LogBuffer(int size) {
        this.builder = new StringBuilder(size);
    }
    
    public LogBuffer add(String msg) {
        builder.append(msg);
        return this;
    }
    
    public LogBuffer add(Number val) {
        builder.append(val.toString());
        return this;
    }
    
    public void clear() {
        builder.delete(0, builder.length());
    }
    
    public int length() {
        return builder.length();
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
