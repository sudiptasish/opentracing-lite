/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLExtractor;
import com.sc.hm.otl.core.OTLInjector;
import io.opentracing.propagation.Format;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

/**
 *
 * @author Sudiptasish Chanda
 */
public class CarrierRegistryTest {
    
    @Test
    public void testGetExtractor() {
        CarrierRegistry registry = new CarrierRegistry();
        OTLExtractor extractor = registry.getExtractor(Format.Builtin.TEXT_MAP_EXTRACT);
        assertNotNull(extractor, "Default extractor must be present");
        assertTrue(extractor instanceof TextMapExtractor
            , "Extractor must be an instance of TextMapExtractor");
        
    }
    
    @Test
    public void testGetInjector() {
        CarrierRegistry registry = new CarrierRegistry();
        OTLInjector injector = registry.getInjector(Format.Builtin.TEXT_MAP_INJECT);
        assertNotNull(injector, "Default injector must be present");
        assertTrue(injector instanceof TextMapInjector
            , "Injector must be an instance of TextMapInjector");
        
    }
    
    @Test
    public void testRegisterExtractor() {
        OTLExtractor cExtractor = mock(OTLExtractor.class);
        
        CarrierRegistry registry = new CarrierRegistry();
        registry.registerExtractor(Format.Builtin.HTTP_HEADERS, cExtractor);
        
        OTLExtractor extractor = registry.getExtractor(Format.Builtin.TEXT_MAP_EXTRACT);
        assertNotNull(extractor, "Default extractor must be present");
        assertTrue(extractor instanceof TextMapExtractor
            , "Extractor must be an instance of TextMapExtractor");
        
        extractor = registry.getExtractor(Format.Builtin.HTTP_HEADERS);
        assertNotNull(extractor, "Custom HTTP_HEADERS extractor must be present");
        assertTrue(extractor instanceof OTLExtractor
            , "Extractor must be an instance of OTLExtractor");
    }
    
    @Test
    public void testRegisterInjector() {
        OTLInjector cInjector = mock(OTLInjector.class);
        
        CarrierRegistry registry = new CarrierRegistry();
        registry.registerInjector(Format.Builtin.HTTP_HEADERS, cInjector);
        
        OTLInjector injector = registry.getInjector(Format.Builtin.TEXT_MAP_INJECT);
        assertNotNull(injector, "Default injector must be present");
        assertTrue(injector instanceof TextMapInjector
            , "Extractor must be an instance of TextMapInjector");
        
        injector = registry.getInjector(Format.Builtin.HTTP_HEADERS);
        assertNotNull(injector, "Custom HTTP_HEADERS injector must be present");
        assertTrue(injector instanceof OTLInjector
            , "Extractor must be an instance of OTLInjector");
    }
}
