/*
 *     Copyright 2020 Opentracing-LiTE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
