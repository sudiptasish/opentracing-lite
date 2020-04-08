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
package io.opns.otl.core.impl;

import io.opns.otl.core.impl.CarrierRegistry;
import io.opns.otl.core.impl.TextMapInjector;
import io.opns.otl.core.impl.TextMapExtractor;
import io.opns.otl.core.OTLExtractor;
import io.opns.otl.core.OTLInjector;
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
