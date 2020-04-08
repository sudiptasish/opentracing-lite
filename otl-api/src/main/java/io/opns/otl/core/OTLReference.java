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
package io.opns.otl.core;

import io.opentracing.SpanContext;

/**
 * Interface that represents a reference type.
 * 
 * <p>
 * As of now there can be two kind of references:
 *
 * 1. CHILD_OF (default): A Span may be the ChildOf a parent Span. 
 *    In a ChildOf reference, the parent Span depends on the child Span
 *    in some capacity. When a new span is created, the currently
 *    active span would automatically become a parent span and thus
 *    a CHILD_OF relationship is built.
 * 
 * 2. FOLLOWS_FROM: Some parent Spans do not depend in any way on the
 *    result of their child Spans. In these cases, we say merely that
 *    the child Span FollowsFrom the parent Span in a causal sense.
 *    If a Scope exists when the developer creates a new Span then it
 *    will act as its parent, but once the programmer invokes ignoreActiveSpan()
 *    during buildSpan() time, then the new span will be treated as a
 *    FOLLOWS_FROM span.
 * 
 * Once a span has been created, then the reference object will be created to
 * identify the kind fo relationship the newly created span and it's parent might 
 * have. If no active span exist, then the reference will be null.
 * 
 * <p>
 * Example 1: Creating CHILD_OF relationship:
 * 
 * <pre>
 * {@code
 *     Tracer tracer = ...
 * 
 *     public Client restClient() {
 *         Span span = tracer.buildSpan("...")
 *             .asChildOf(context)
 *             .start();
 *     }
 * }
 * </pre>
 * 
 * Because no relationship/reference type is provided, therefore the a CHILD_OF
 * relationship will be established between the parent and the child (newly created)
 * span.
 * 
 * <p>
 * Example 1: Creating FOLLOWS_FROM relationship:
 * 
 * <pre>
 * {@code
 *     Tracer tracer = ...
 * 
 *     public Client restClient() {
 *         Span span = tracer.buildSpan("...")
 *             .addReference(References.FOLLOWS_FROM, context)
 *             .start();
 *     }
 * }
 * </pre>
 * 
 * Here a FOLLOWS_FROM relationship will be established between the parent and
 * the child (newly created) span.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLReference {
    
    /**
     * Return the reference type of the underlying span context with the current span.
     * Supported reference types are:
     * 1. CHILD_OF
     * 2. FOLLOWS_FROM
     * 
     * @return String
     */
    String type();
    
    /**
     * Return the parent span context, with which the said reference is built.
     * @return SpanContext
     */
    OTLSpanContext context();
}
