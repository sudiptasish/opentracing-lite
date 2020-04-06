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
package com.sc.hm.otl.slf4j.spi;

import com.sc.hm.otl.core.ctx.ContextAdapter;
import java.util.Map;
import org.slf4j.MDC;

/**
 * Slf4J context adapter implementation.
 * 
 * If the underlying logging system offers MDC functionality, then SLF4J's MDC, 
 * i.e. this class, will delegate to the underlying system's MDC. Note that at 
 * this time, only two logging systems, namely log4j and logback, offer MDC functionality.
 * Whatever contextual info is set by this class, can later be extracted by the 
 * underlying logger framework, with the help <code>%X</code> and dumped to the
 * standard output/file.
 *
 * @author Sudiptasish Chanda
 */
public class Slf4JContextAdapter implements ContextAdapter {
    
    @Override
    public void put(String key, String value) {
        MDC.put(key, value);
    }

    @Override
    public String get(String key) {
        return MDC.get(key);
    }

    @Override
    public void remove(String key) {
        MDC.remove(key);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    @Override
    public void put(Map<String, String> ctxMap) {
        MDC.setContextMap(ctxMap);
    }
}
