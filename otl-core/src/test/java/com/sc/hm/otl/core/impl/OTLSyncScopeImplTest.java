/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSyncScope;
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
                , new OTLSpanImpl(tracer, "testCreateValidScope", null, null, true, 0)
                , null);
        
        assertNotNull(scope, "Scope must be non-null");
        
        OTLSpan span = (OTLSpan)scopeManager.activeSpan();
        assertEquals("testCreateValidScope", span.operation());
    }
}
