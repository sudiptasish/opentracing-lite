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
package io.opns.otl.core.ctx;

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
