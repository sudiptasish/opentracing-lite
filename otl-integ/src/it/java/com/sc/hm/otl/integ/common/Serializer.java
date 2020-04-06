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
package com.sc.hm.otl.integ.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Serializer class.
 * This class has the APIs for serializing/deserializing the object to
 * produce appropriate output.
 *
 * It internally uses {@link ObjectMapper} for (de)serialization.
 */
public final class Serializer {

    // Object Mapper instance is thread safe.
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Deserialize this json to construct and object of the type specified.
     *
     * @param json  JSON string
     * @param clazz Type of the object this json data will be deserialized to.
     * @param <T>
     * @return T
     * @throws IOException
     */
    public static <T> T deserialize(String json, Class<T> clazz) throws IOException {
        return deserialize(json.getBytes(), clazz);
    }

    /**
     * Deserialize this input stream to construct and object of the type specified.
     *
     * @param in    Input stream
     * @param clazz Type of the object this json data will be de-serialized to.
     * @param <T>
     * @return T
     * @throws IOException
     */
    public static <T> T deserialize(InputStream in, Class<T> clazz) throws IOException {
        return mapper.readValue(in, clazz);
    }

    /**
     * Deserialize this json to construct and object of the type specified.
     *
     * @param jsonArray RAW JSON array
     * @param clazz     Type of the object this json data will be deserialized to.
     * @param <T>
     * @return T
     * @throws IOException
     */
    public static <T> T deserialize(byte[] jsonArray, Class<T> clazz) throws IOException {
        return mapper.readValue(jsonArray, clazz);
    }

    /**
     * Serialize the java object to raw json string.
     *
     * @param object The POJO that will be serialize to json string.
     *              It can be a simple hashmap as well.
     * @return String JSON String representing the specified object.
     * @throws IOException
     */
    public static String serialize(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }
}
