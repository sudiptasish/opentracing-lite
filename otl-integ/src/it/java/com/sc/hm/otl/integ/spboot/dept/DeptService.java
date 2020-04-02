/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.spboot.dept;

import com.sc.hm.otl.integ.common.Storage;
import com.sc.hm.otl.integ.model.Department;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Sudiptasish Chanda
 */
@Service
public class DeptService {
    
    private final Logger logger = LoggerFactory.getLogger(DeptService.class);
    
    private final Tracer tracer = GlobalTracer.get();
    private final Storage<String, Department> storage = Storage.DEPT_STORAGE;
    
    public void insert(Department dept) {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("DeptService::insert").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            storage.add(dept.getId(), dept);
            if (logger.isInfoEnabled()) {
                logger.info("Added department {} to storage", dept);
            }
        }
        finally {
            span.finish();
        }
    }
    
    public Department select(String id) {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("DeptService::select").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            if (logger.isInfoEnabled()) {
                logger.info("Fetching department for id {} from storage", id);
            }
            return storage.get(id);
        }
        finally {
            span.finish();
        }
    }
    
    public Collection<Department> selectAll() {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("DeptService::selectAll").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            if (logger.isInfoEnabled()) {
                logger.info("Fetching All departments from storage");
            }
            return storage.values();
        }
        finally {
            span.finish();
        }
    }
}
