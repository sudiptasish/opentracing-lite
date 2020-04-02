/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.jaxrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
public class DeploymentConfig {
    
    private final String cotextRoot;
    
    private final List<String> filterNames = new ArrayList<>();
    private final List<String> filters = new ArrayList<>();
    private final Map<String, String> filterMapping = new HashMap<>();
    private final Map<String, Map<String, String>> filterParams = new HashMap<>();
    
    private final List<String> servletNames = new ArrayList<>();
    private final List<String> servlets = new ArrayList<>();
    private final Map<String, String> servletMapping = new HashMap<>();
    private final Map<String, Map<String, String>> servletParams = new HashMap<>();

    public DeploymentConfig(String cotextRoot) {
        this.cotextRoot = cotextRoot;
    }

    public String getCotextRoot() {
        return cotextRoot;
    }
    
    public void addFilter(String filterName, String filter, String mapping, Map<String, String> params) {
        filterNames.add(filterName);
        filters.add(filter);
        filterMapping.put(filterName, mapping);
        filterParams.put(filterName, params);
    }
    
    public void addServlet(String servletName, String servlet, String mapping, Map<String, String> params) {
        servletNames.add(servletName);
        servlets.add(servlet);
        servletMapping.put(servletName, mapping);
        servletParams.put(servletName, params);
    }

    public List<String> getFilterNames() {
        return filterNames;
    }

    public List<String> getFilters() {
        return filters;
    }

    public Map<String, String> getFilterMapping() {
        return filterMapping;
    }

    public Map<String, Map<String, String>> getFilterParams() {
        return filterParams;
    }

    public List<String> getServletNames() {
        return servletNames;
    }

    public List<String> getServlets() {
        return servlets;
    }

    public Map<String, String> getServletMapping() {
        return servletMapping;
    }

    public Map<String, Map<String, String>> getServletParams() {
        return servletParams;
    }
}
