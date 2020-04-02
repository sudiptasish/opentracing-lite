/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.metric;

import java.util.ServiceLoader;

/**
 * Interface to represent a third party metric sub-system provider.
 * 
 * Using the java's {@link ServiceLoader} capability the third party metric provider
 * is loaded into memory. Which can later be used to instantiate the platform
 * specific {@link MetricAdapter}. If no provider is found, then the default
 * context adapter will be used.
 *
 * @author Sudiptasish Chanda
 */
public interface MetricProvider {
    
    /**
     * Instantiate the platform specific metric adapter.
     * @return MetricAdapter
     */
    MetricAdapter create();
}
