/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

import com.sc.hm.otl.core.log.LoggerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sudiptasish Chanda
 */
public class MockLoggerAdapter implements LoggerAdapter {
    
    private final List<String> msgs = new ArrayList<>();

    @Override
    public void log(String msg) {
        msgs.add(msg);
    }

    public List<String> getLogMessages() {
        return msgs;
    }
}
