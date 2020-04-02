/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.log;

import java.io.PrintStream;

/**
 * A console logger adapter, which dumps any span data onto the standard output.
 *
 * @author Sudiptasish Chanda
 */
public class ConsoleLoggerAdapter implements LoggerAdapter {
    
    private final PrintStream console = System.out;

    @Override
    public void log(String msg) {
        console.println(msg);
    }
    
}
