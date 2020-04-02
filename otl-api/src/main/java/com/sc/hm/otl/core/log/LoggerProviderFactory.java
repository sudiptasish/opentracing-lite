/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.log;

import com.sc.hm.otl.core.ctx.ContextProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Factory class to find a {@link LoggerProvider}.
 * It uses java's {@link ServiceLoader} mechanism to find any registered provider.
 * It is, however, possible that there are multiple providers found, in which case
 * it will take the very first provider appearing in the classpath.
 *
 * @author Sudiptasish Chanda
 */
public class LoggerProviderFactory {
    
    private static volatile boolean INITIALIZED = false;
    
    private static volatile LoggerProvider LOGGER_PROVIDER;
    
    public static LoggerProvider getProvider() {
        return getProvider(false);
    }
    
    public static LoggerProvider getProvider(boolean refresh) {
        if (!INITIALIZED || refresh) {
            synchronized(ContextProvider.class) {
                if (INITIALIZED && !refresh) {
                    return LOGGER_PROVIDER;
                }
                ServiceLoader<LoggerProvider> loader = ServiceLoader.load(LoggerProvider.class);
                if (refresh) {
                    loader.reload();
                }
                List<LoggerProvider> providers = new ArrayList<>(1);
                for (LoggerProvider provider : loader) {
                    providers.add(provider);
                }
                INITIALIZED = true;
                if (!providers.isEmpty()) {
                    LOGGER_PROVIDER = providers.get(0);
                }
            }
        }
        return LOGGER_PROVIDER;
    }
}
