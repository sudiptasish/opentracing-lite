/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author Sudiptasish Chanda
 */
public class MockFilterChain implements FilterChain {

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1) throws IOException, ServletException {
        // Do nothing
    }
    
}
