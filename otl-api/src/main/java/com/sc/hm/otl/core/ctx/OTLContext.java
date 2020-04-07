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
package com.sc.hm.otl.core.ctx;

/**
 * This class hides and serves as a substitute for the underlying thread context
 * implementation of any provider.
 * 
 * <p>
 * If the underlying logging provider the feature of storing contextual information
 * of a thread, this class, will delegate all the calls to the underlying system's 
 * thread context.
 * It accomplishes the same with the help of {@link ContextAdapter}. If the
 * associated framework provides an adapter then the same will be loaded by the
 * provider library. If no adapter is found, then the default {@link NoOpContextAdapter}
 * will be used.
 *
 * <p>
 * Thus, as a context user, one can take advantage of thread context in the presence
 * of any logging framework.
 * 
 * <p>
 * Please note that all methods in this class are static.
 * 
 * @author Sudiptasish Chanda
 */
public final class OTLContext {
    
    private static final ContextAdapter CTX_ADAPTER;
    
    static {
        ContextProvider provider = ContextProviderFactory.getProvider();
        if (provider != null) {
            CTX_ADAPTER = provider.create();
        }
        else {
            CTX_ADAPTER = new NoOpContextAdapter();
        }
    }
    
    /**
     * Put a context value as identified with the key parameter into the current
     * thread's diagnostic context map. The <code>key</code> parameter cannot be null.
     * The parameter, however, can be null only if the underlying implementation supports it.
     *
     * @param key non-null key 
     * @param val value to put in the map
     * 
     * @throws IllegalArgumentException
     *           in case the "key" parameter is null
     */
    public static void put(String key, String val) {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        CTX_ADAPTER.put(key, val);
    }

    /**
     * Get the context value as identified by the key parameter. The key parameter
     * cannot be null.
     *
     * @param key a key
     * @return the string value identified by the <code>key</code> parameter.
     * @throws IllegalArgumentException
     *           in case the "key" parameter is null
     */
    public static String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        return CTX_ADAPTER.get(key);
    }

    /**
     * Remove the diagnostic context identified by the key parameter from the
     * underlying thread's context.
     *
     * @param key  a key
     * @throws IllegalArgumentException
     *           in case the "key" parameter is null
     */
    public static void remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        CTX_ADAPTER.remove(key);
    }

    /**
     * Clear all entries in the MDC of the underlying implementation.
     */
    public static void clear() {
        CTX_ADAPTER.clear();
    }
}
