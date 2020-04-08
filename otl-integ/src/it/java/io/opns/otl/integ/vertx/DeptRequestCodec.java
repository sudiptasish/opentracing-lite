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
package io.opns.otl.integ.vertx;

import io.opns.otl.integ.model.Department;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/**
 *
 * @author schan280
 */
public class DeptRequestCodec implements MessageCodec<Department, Department> {

    @Override
    public void encodeToWire(Buffer buffer, Department s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Department decodeFromWire(int pos, Buffer buffer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Department transform(Department s) {
        return s;
    }

    @Override
    public String name() {
        return DeptRequestCodec.class.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
    
}
