/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
