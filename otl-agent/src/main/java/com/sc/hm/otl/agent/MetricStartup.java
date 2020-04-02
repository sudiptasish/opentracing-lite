/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


/**
 * Class that help registering otl metric sub-system with the VM.
 *
 * @author Sudiptasish Chanda
 */
public class MetricStartup {
    
    private final String METRIC_LIB = "otl-metrics";
    private final String METRIC_BOOTSTRAP = "com.sc.hm.otl.metrics.server.MetricBootstrap";
    
    /**
     * Register the otl metric system with the VM.
     * 
     * The metrics collection will be enabled only if the otl-metrics.version.jar
     * is found in the classpath. If no jar is found in the classpath, then it will
     * be assumed that user has not included the metric jar in runtime, and thus 
     * no metric will ever be collected.
     */
    public void start() {
        try {
            Class clazz = Class.forName(METRIC_BOOTSTRAP);
            Constructor constructor = clazz.getDeclaredConstructor(new Class[] {});
            Object obj = constructor.newInstance(new Object[] {});
            
            // Invoke the method.
            Method method = clazz.getDeclaredMethod("init", new Class[] {});
            method.invoke(obj, new Object[] {});
            
            System.out.println("Successfully initialized metric sub-system");
        }
        catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                System.err.println("Metric sub-system otl-metrics.jar is not"
                    + " found in the classpath.");
            }
            else {
                e.printStackTrace();
            }
        }
    }
}
