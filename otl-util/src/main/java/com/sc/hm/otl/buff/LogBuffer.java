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
package com.sc.hm.otl.buff;

/**
 *
 * @author Sudiptasish Chanda
 */
public class LogBuffer {
    
    private final StringBuilder builder;
    
    public LogBuffer() {
        this(256);
    }
    
    public LogBuffer(int size) {
        this.builder = new StringBuilder(size);
    }
    
    public LogBuffer add(String msg) {
        builder.append(msg);
        return this;
    }
    
    public LogBuffer add(Number val) {
        builder.append(val.toString());
        return this;
    }
    
    public void clear() {
        builder.delete(0, builder.length());
    }
    
    public int length() {
        return builder.length();
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
