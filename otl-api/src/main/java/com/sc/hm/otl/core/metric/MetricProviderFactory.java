/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.metric;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Factory class to find a {@link MetricProvider}.
 * It uses java's {@link ServiceLoader} mechanism to find any registered provider.
 * It is, however, possible that there are multiple providers found, in which case
 * it will take the very first provider appearing in the classpath.
 *
 * @author Sudiptasish Chanda
 */
public class MetricProviderFactory {
    
    private static volatile boolean INITIALIZED = false;
    
    private static volatile MetricProvider CTX_PROVIDER;
    
    public static MetricProvider getProvider() {
        return getProvider(false);
    }
    
    public static MetricProvider getProvider(boolean refresh) {
        if (!INITIALIZED || refresh) {
            synchronized(MetricProvider.class) {
                ServiceLoader<MetricProvider> loader = ServiceLoader.load(MetricProvider.class);
                if (refresh) {
                    loader.reload();
                }
                List<MetricProvider> providers = new ArrayList<>(1);
                for (MetricProvider provider : loader) {
                    providers.add(provider);
                }
                INITIALIZED = true;
                if (!providers.isEmpty()) {
                    CTX_PROVIDER = providers.get(0);
                }
            }
        }
        return CTX_PROVIDER;
    }
}
