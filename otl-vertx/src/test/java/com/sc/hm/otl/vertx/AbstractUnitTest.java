/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.impl.OTLProvider;
import com.sc.hm.otl.core.impl.OTLTracer;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeAll;

/**
 *
 * @author Sudiptasish Chanda
 */
public abstract class AbstractUnitTest {
    
    protected static Tracer tracer;
    
    @BeforeAll
    public static void init() {
        System.setProperty("span.visitor", "com.sc.hm.otl.vertx.MockSpanVisitor");
        initTracer();
    }
    
    private static void initTracer() {
        try {
            GlobalTracer.registerIfAbsent(new OTLProvider().createTracer());
        }
        catch (IllegalStateException e) {
            // Tracer already initialized
        }
        tracer = GlobalTracer.get();
    }
    
    protected OTLSpanVisitor extract() {
        try {
            Field field = GlobalTracer.class.getDeclaredField("tracer");
            field.setAccessible(true);
            OTLTracer otlTracer = (OTLTracer)field.get(tracer);
            return otlTracer.visitor();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
