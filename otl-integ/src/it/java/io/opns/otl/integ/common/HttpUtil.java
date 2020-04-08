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
package io.opns.otl.integ.common;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class to make outbound http request, using native java API.
 * 
 * @author Sudiptasish Chanda
 */
public final class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    
    /**
     * API that uses native java library to make a remote request.
     * The response will be captured as a raw byte array.
     * It's the responsibility of the client/caller to deserialize
     * the response.
     *
     * @param urlName       The remote service URL.
     * @param method        Http Method (GET/POST/DELETE, etc)
     * @param payload       Request body, in case of a POST request.
     * @param result        Result will hold the response from the remote service
     *                     (in raw byte array format).
     * @param headers       Http headers
     *
     * @return int          Http status code.
     */
    public static int rpc(String urlName,
            String method,
            Object payload,
            ByteArrayOutputStream result,
            Map<String, String> headers) {

        URL url = null;
        HttpURLConnection httpConn = null;

        try {
            // Connect to the remote billing
            url = new URL(urlName);
            httpConn = (HttpURLConnection) url.openConnection();

            // Set the request method (GET/POST/DELETE)
            httpConn.setRequestMethod(method);
            if (headers != null) {
                for (Map.Entry<String, String> me : headers.entrySet()) {
                    httpConn.setRequestProperty(me.getKey(), me.getValue());
                }
            }
            // For GET/POST, we assume that the media type is json.
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Accept", "application/json");
                
            if ("POST".equalsIgnoreCase(method)) {
                httpConn.setDoOutput(Boolean.TRUE);

                DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
                out.write(Serializer.serialize(payload).getBytes());
                out.flush();
                out.close();
            }

            // Check the status code:
            int code = httpConn.getResponseCode();

            // Read the response ...
            DataInputStream in = getReaderStream(httpConn);
            if (in != null) {
                byte[] buff = new byte[1024];
                int read = -1;

                while ((read = in.read(buff)) != -1) {
                    result.write(buff, 0, read);
                }
                in.close();
            }
            return code;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
    }

    /**
     * Return the input stream from the underlying url connection.
     * This stream can be used to read any data that is sent by the
     * remote service.
     *
     * If the response code is SUCCESS (200 or 300 family), then the
     * input stream can be obtained from {@code HttpURLConnection.getInputStream}
     * , otherwise the input stream will be obtained from
     * {@code HttpURLConnection.getErrorStream} . Above is the default
     * behavior of java HttpURLConnection.
     *
     * @param httpConn
     * @return DataInputStream
     * @throws IOException
     */
    private static DataInputStream getReaderStream(HttpURLConnection httpConn) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Response code from {} is {}"
                    , httpConn.getURL()
                    , httpConn.getResponseCode());
        }
        if (httpConn.getResponseCode() == 404) {
            if (httpConn.getErrorStream() == null) {
                return null;
            }
            return new DataInputStream(httpConn.getErrorStream());
        }
        if (httpConn.getResponseCode() < 400) {
            return new DataInputStream(httpConn.getInputStream());
        } else {
            if (httpConn.getErrorStream() == null) {
                return null;
            }
            return new DataInputStream(httpConn.getErrorStream());
        }
    }

    private static boolean isPrimitive(Object obj) {
        Class<?> type = obj.getClass();

        return type == String.class
                || type == Character.class
                || type == Byte.class
                || type == Integer.class
                || type == Long.class
                || type == Float.class
                || type == Double.class;
    }
}
