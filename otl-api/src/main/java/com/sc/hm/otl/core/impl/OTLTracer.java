/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLReference;
import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanContext;
import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.metric.EventSource;
import com.sc.hm.otl.core.metric.EventType;
import com.sc.hm.otl.core.metric.MetricExtension;
import io.opentracing.References;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A light weight tracer interface.
 * 
 * It extends the {@link Tracer} interface provided by opentracing. Additionally,
 * it provides other APIs to perform specific operation.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLTracer extends Tracer {

    /**
     * Activate the span in an async environment.
     * 
     * @param span
     * @return 
     */
    Scope activateAsync(Span span);
    
    /**
     * This is the method to resume a span.
     * 
     * <p>
     * Resuming a span can happen in case of asynchronous data transfer operation.
     * Let's consider the below flow:
     * 1. Application A is trying to send some event to Application B.
     * 2. The only way to communicate between them is to send persistent message.
     * 3. If Application B is down then the messages will be kept in a queue.
     * 4. Once the Application B comes up, it will start consuming the messages
     *    from the queue.
     * 
     * If Application A is trying to send some contextual info to Application B,
     * it can do so by injecting the appropriate header(s) in the message. Now
     * it may take some time for Application B to consume the message, and till
     * that time, the message would be lying in the queue. If we want to represent
     * the amount of time the message sitting idle in the queue, we need a span for that.
     * 
     * One way to achieve this is that Application A can create a span and inject
     * the context data in the mesaeg header, it has to ensure that the span is
     * never closed. So what is the status of this span then? We can say, that it
     * is now serialized (in dormant state). Remember, that a span can only exist
     * in the memory.
     * 
     * Now once Application B receives the message, it can deserialize the span
     * context and mark it as active (thus resuming the span again). Because this
     * span represents the idle wait time of a message, therefore it must be
     * closed immediately.
     * 
     * <p>For example:
     * <pre><code>
     *   Tracer tracer = ...
     *
     *   SpanContext context = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT, new XXXAdapter(message));
     *   Span span = tracer.resumeSpan(context)
     *                     .withTag("ABC", "XYZ")
     *                     .start();
     *   
     * </code></pre>
     * 
     * @param context
     * @return SpanBuilder
     */
    Tracer.SpanBuilder resumeSpan(SpanContext context);
    
    /**
     * Return the span visitor.
     * @return OTLSpanVisitor
     */
    OTLSpanVisitor visitor();
    
    public class SpanBuilder implements Tracer.SpanBuilder {
        
        private final OTLTracer tracer;
        private final String operation;
        
        private final List<OTLReference> references = new ArrayList<>(1);
        
        private final Map<String, Object> tags = new HashMap<>();
        private boolean ignoreActive;
        private long startTime = System.nanoTime() / 1000;  // Micro seconds
        
        private Object callback;
        
        private boolean childInvoked = false;
        
        public SpanBuilder(OTLTracer tracer, String operation) {
            this.tracer = tracer;
            this.operation = operation;
        }

        @Override
        public SpanBuilder asChildOf(SpanContext parent) {
            return addReference(References.CHILD_OF, parent);
        }

        @Override
        public SpanBuilder asChildOf(Span parent) {
            return addReference(References.CHILD_OF, parent != null ? parent.context() : null);
        }

        @Override
        public SpanBuilder addReference(String refType, SpanContext refCtx) {
            // As of now there can be two kind of references:
            //
            // 1. CHILD_OF (default): A Span may be the ChildOf a parent Span. 
            //    In a ChildOf reference, the parent Span depends on the child Span
            //    in some capacity. When a new span is created, the currently
            //    active span would automatically become a parent span and thus
            //    a CHILD_OF relationship is built.
            // 2. FOLLOWS_FROM: Some parent Spans do not depend in any way on the
            //    result of their child Spans. In these cases, we say merely that
            //    the child Span FollowsFrom the parent Span in a causal sense.
            //    If a Scope exists when the developer creates a new Span then it
            //    will act as its parent, but once the programmer invokes ignoreActiveSpan()
            //    during buildSpan() time, then the new span will be treated as a
            //    FOLLOWS_FROM span.
            
            childInvoked = true;
            if (refCtx == null) {
                return this;
            }
            
            OTLReference ref = new OTLReferenceImpl(refType, (OTLSpanContext)refCtx);
            for (OTLReference reference : references) {
                // You should not have more than one CHILD_OF relationship, however,
                // multiple FOLLOWS_FORM relationship is allowed.
                if (reference.equals(ref) && ref.type().equals(References.CHILD_OF)) {
                    throw new IllegalStateException("Reference " + refCtx + " already exist");
                }
                // Same spanId cannot exist in a CHILD_OF as well as in a FOLLOWS_FORM relation.
                if (reference.context().toSpanId().equals(ref.context().toSpanId())) {
                    throw new IllegalStateException("Span Id " + ref.context().toSpanId()
                        + " already exist as " + reference.type());
                }
            }
            references.add(ref);
            return this;
        }

        @Override
        public SpanBuilder ignoreActiveSpan() {
            this.ignoreActive = true;
            return this;
        }

        @Override
        public SpanBuilder withTag(String key, String value) {
            this.tags.put(key, value);
            return this;
        }

        @Override
        public SpanBuilder withTag(String key, boolean value) {
            this.tags.put(key, value);
            return this;
        }

        @Override
        public SpanBuilder withTag(String key, Number value) {
            this.tags.put(key, value);
            return this;
        }

        @Override
        public <T> SpanBuilder withTag(Tag<T> tag, T value) {
            this.tags.put(tag.getKey(), value);
            return this;
        }

        @Override
        public SpanBuilder withStartTimestamp(long microseconds) {
            this.startTime = microseconds;
            return this;
        }
        
        public SpanBuilder withCallback(Object callback) {
            this.callback = callback;
            return this;
        }

        @Override
        public OTLSpan start() {
            // Check if the reference is populated. If not, populate it.
            // It is possible that user may not have invoked the method asChildOf(...)
            // or addReference(...) at the time of creating the span.
            // 
            // They might have created a span like:
            // Span span = tracer.buildSpan("...").start()
            //
            // In which case, if a span already exist in the current scope, it will
            // automatically become the parent of the newly created span, and a 
            // CHILD_OF relationship would be established.
            if (!childInvoked) {
                Span currentSpan = tracer.activeSpan();
                if (currentSpan != null) {
                    SpanContext spanCtx = currentSpan.context();
                    addReference(References.CHILD_OF, spanCtx);
                }
            }
            
            // Note that starting a span will not necessarily activate it.
            OTLSpan span = new OTLSpanImpl(tracer
                , operation
                , references
                , tags
                , callback
                , ignoreActive
                , startTime);
            
            // Fire the span creation event.
            MetricExtension.fireEvent(EventSource.SPAN
                , EventType.SPAN_CREATED
                , (System.nanoTime() / 1000 - startTime));
            
            return span;
        }
    }
}
