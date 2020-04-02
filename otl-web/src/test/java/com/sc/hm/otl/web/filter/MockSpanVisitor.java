/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanVisitor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sudiptasish Chanda
 */
public class MockSpanVisitor implements OTLSpanVisitor {
    
    private final List<OTLSpan> spans = new ArrayList<>();

    @Override
    public void visit(OTLSpan span, Object param) {
        spans.add(span);
    }

    public List<OTLSpan> getSpans() {
        return spans;
    }
}
