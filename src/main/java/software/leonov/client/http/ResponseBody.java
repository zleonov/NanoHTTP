/*
 * Copyright (C) 2020 Zhenya Leonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.leonov.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * The {@code Message-Body} of an HTTP response.
 * <p>
 * <b>Note:</b> Whether or not a {@code Message-Body} is buffered in the response is implementation dependent. Unless
 * otherwise stated, it should be assumed that this {@code ResponseBody} object represents a <i>one-shot</i> stream
 * backed by an active connection to the server, and may be consumed only <b>once</b> by calling the the
 * {@link ResponseBody#asString()}, {@link ResponseBody#toByteArray()}, or {@link ResponseBody#getInputStream()}
 * methods.
 * <p>
 * The response body <i>should</i> be consumed in it's entirety to allow the underlying {@code Connection} to be reused,
 * assuming <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/net/http-keepalive.html" target=
 * "_blank">keep-alive</a> is on. All underlying streams will be closed when the response is {@link HttpResponse#close()
 * closed}.
 * 
 * @author Zhenya Leonov
 */
public interface ResponseBody {

    /**
     * Returns an input stream which reads this {@code Message-Body}.
     * <p>
     * A {@code SocketTimeoutException} can be thrown when reading from the returned input stream if the read timeout
     * expires before data is available for read.
     * 
     * @return an input stream which reads this {@code Message-Body}
     * @throws IOException if an I/O error occurs
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Parses and returns this {@code Message-Body} as a string. The charset used is determined by
     * {@link HttpResponse#getContentCharset()}.
     * 
     * @return this {@code Message-Body} as a string
     * @throws IOException if an I/O error occurs
     */
    public String asString() throws IOException;

    /**
     * Parses and returns this {@code Message-Body} as a string using the specified charset.
     * <p>
     * As stated in <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.5" target="_blank">RFC-7231</a>: <i>In
     * practice, resource owners do not always properly configure their origin server to provide the correct Content-Type
     * for a given representation.</i> This method allows users to override {@link HttpResponse#getContentCharset()
     * Content-Type charset} returned by the server.
     * 
     * @return this {@code Message-Body} as a string
     * @throws IOException if an I/O error occurs
     */
    public String asString(final Charset charset) throws IOException;

    /**
     * Returns a byte array containing all the bytes read from this {@code Message-Body}.
     * 
     * @return a byte array containing all the bytes read from this {@code Message-Body}
     * @throws IOException if an I/O error occurs
     */
    public byte[] toByteArray() throws IOException;

}
