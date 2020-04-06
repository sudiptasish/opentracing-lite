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
package com.sc.hm.otl.core.impl;

import io.opentracing.propagation.Format;
import java.util.HashMap;
import java.util.Map;
import com.sc.hm.otl.core.OTLExtractor;
import com.sc.hm.otl.core.OTLInjector;

/**
 *
 * @author Sudiptasish Chanda
 */
public class CarrierRegistry {
    
    private final Map<Format, OTLExtractor> extractorMapping = new HashMap<>();
    private final Map<Format, OTLInjector> injectorMapping = new HashMap<>();
    
    CarrierRegistry() {
        initDefault();
    }
    
    private void initDefault() {
        extractorMapping.put(Format.Builtin.TEXT_MAP_EXTRACT, new TextMapExtractor());
        injectorMapping.put(Format.Builtin.TEXT_MAP_INJECT, new TextMapInjector());
    }
    
    public void registerExtractor(Format format, OTLExtractor extractor) {
        extractorMapping.put(format, extractor);
    }
    
    public void registerInjector(Format format, OTLInjector injector) {
        injectorMapping.put(format, injector);
    }
    
    public OTLExtractor getExtractor(Format format) {
        return extractorMapping.get(format);
    }
    
    public OTLInjector getInjector(Format format) {
        return injectorMapping.get(format);
    }

    @Override
    public String toString() {
        return "OTLRegistry=> Extractor: " + extractorMapping + ". Injector: " + injectorMapping;
    }
}
