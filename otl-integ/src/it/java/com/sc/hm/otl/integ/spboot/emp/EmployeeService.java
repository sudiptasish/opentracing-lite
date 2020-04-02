/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.spboot.emp;

import com.sc.hm.otl.integ.common.Storage;
import com.sc.hm.otl.integ.model.Employee;
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
public class EmployeeService {
    
    private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    
    private final Tracer tracer = GlobalTracer.get();
    private final Storage<String, Employee> storage = Storage.getEmpDB();
    
    public void insert(Employee emp) {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("EmployeeService::insert").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            storage.add(emp.getId(), emp);
            if (logger.isInfoEnabled()) {
                logger.info("Added employee {} to storage {}. Current count: {}"
                    , emp, this, storage.size());
            }
        }
        finally {
            span.finish();
        }
    }
    
    public Employee select(String id) {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("EmployeeService::select").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            if (logger.isInfoEnabled()) {
                logger.info("Fetching employee for id {} from storage {}. Current count: {}"
                    , id, this, storage.size());
            }
            return storage.get(id);
        }
        finally {
            span.finish();
        }
    }
    
    public Collection<Employee> selectAll() {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("EmployeeService::selectAll").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            if (logger.isInfoEnabled()) {
                logger.info("Fetching All employees from storage");
            }
            return storage.values();
        }
        finally {
            span.finish();
        }
    }
}
