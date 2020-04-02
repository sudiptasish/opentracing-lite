/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.ctx;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Factory class to find a {@link ContextProvider}.
 * It uses java's {@link ServiceLoader} mechanism to find any registered provider.
 * It is, however, possible that there are multiple providers found, in which case
 * it will take the very first provider appearing in the classpath.
 *
 * @author Sudiptasish Chanda
 */
public class ContextProviderFactory {
    
    private static volatile boolean INITIALIZED = false;
    
    private static volatile ContextProvider CTX_PROVIDER;
    
    public static ContextProvider getProvider() {
        return getProvider(false);
    }
    
    public static ContextProvider getProvider(boolean refresh) {
        if (!INITIALIZED || refresh) {
            synchronized(ContextProvider.class) {
                ServiceLoader<ContextProvider> loader = ServiceLoader.load(ContextProvider.class);
                if (refresh) {
                    loader.reload();
                }
                List<ContextProvider> providers = new ArrayList<>(1);
                for (ContextProvider provider : loader) {
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
