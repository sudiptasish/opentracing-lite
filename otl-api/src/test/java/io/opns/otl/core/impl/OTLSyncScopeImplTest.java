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

import io.opns.otl.core.impl.OTLTracerImpl;
import io.opns.otl.core.impl.OTLSpanImpl;
import io.opns.otl.core.impl.OTLSyncScopeImpl;
import io.opns.otl.core.impl.OTLSyncScopeManagerImpl;
import io.opns.otl.core.OTLSpan;
import io.opns.otl.core.OTLSyncScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLSyncScopeImplTest {
    
    @Test
    public void testCreateScopeWithInvalidManager() {
        Exception e = assertThrows(
            NullPointerException.class,
            () -> new OTLSyncScopeImpl(null
                , null
                , null));
        
        assertTrue(e.getMessage().equals("Scope Manager must be non-null"));
    }
    
    @Test
    public void testCreateScopeWithInvalidSpan() {
        Exception e = assertThrows(
            NullPointerException.class,
            () -> new OTLSyncScopeImpl(new OTLSyncScopeManagerImpl()
                , null
                , null));
        
        assertTrue(e.getMessage().equals("Span must be non-null"));
    }
    
    @Test
    public void testCreateValidScope() {
        OTLTracerImpl tracer = new OTLTracerImpl();
        OTLSyncScopeManagerImpl scopeManager = (OTLSyncScopeManagerImpl)tracer.scopeManager();
        
        OTLSyncScope scope = new OTLSyncScopeImpl(scopeManager
                , new OTLSpanImpl(tracer, "testCreateValidScope", null, null, null, true, 0)
                , null);
        
        assertNotNull(scope, "Scope must be non-null");
        
        OTLSpan span = (OTLSpan)scopeManager.activeSpan();
        assertEquals("testCreateValidScope", span.operation());
    }
}
