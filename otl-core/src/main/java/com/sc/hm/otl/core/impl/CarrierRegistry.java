/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
