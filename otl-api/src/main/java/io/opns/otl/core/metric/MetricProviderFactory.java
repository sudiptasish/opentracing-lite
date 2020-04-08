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
package io.opns.otl.core.metric;

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
