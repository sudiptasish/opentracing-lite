/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.vertx;

import com.sc.hm.otl.integ.model.Department;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/**
 *
 * @author schan280
 */
public class DeptRequestCodec implements MessageCodec<Department, Department> {

    @Override
    public void encodeToWire(Buffer buffer, Department s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Department decodeFromWire(int pos, Buffer buffer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Department transform(Department s) {
        return s;
    }

    @Override
    public String name() {
        return DeptRequestCodec.class.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
    
}
