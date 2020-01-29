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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * An HTTP request with a {@code Message-Body}: {@code DELETE}, {@code POST}, or {@code PUT}.
 * 
 * @author Zhenya Leonov
 */
public final class HttpRequestWithBody extends HttpRequest {

    private RequestBody body = null;
    private long contentLength = -1;

    HttpRequestWithBody(final String method, final URL url, final Proxy proxy, final HostnameVerifier hostnameVerifier, final SSLSocketFactory sslSocketFactory) throws IOException {
        super(method, url, proxy, hostnameVerifier, sslSocketFactory);
    }

    /**
     * Returns the value {@code Content-Encoding} request header or {@code null} if it is not specified
     * 
     * @param the value of the {@code Content-Encoding} header or {@code null} if it is not specified
     * @return this {@code HttpRequest} instance
     */
    public String getContentEncoding() {
        return getHeader("Content-Encoding");
    }

    /**
     * Returns the value {@code Content-Length} request header or -1 if it is not specified.
     * 
     * @param the value of the {@code Content-Length} header or -1 if it is not specified
     * @return this {@code HttpRequest} instance
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * Returns the value {@code Content-Type} request header or {@code null} if it is not specified.
     * 
     * @param the value of the {@code Content-Type} header or {@code null} if it is not specified
     * @return this {@code HttpRequest} instance
     */
    public String getContentType() {
        return getHeader("Content-Type");
    }

    /**
     * Returns the {@code Message-Body} to send to the server or {@code null} if it is not specified.
     * 
     * @param body the {@code Message-Body} to send to the server or {@code null} if it is not specified
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public RequestBody getBody() {
        return body;
    }

    /**
     * Sends the HTTP request.
     * <p>
     * Unlike {@link HttpRequest#send()} {@code DELETE}, {@code POST}, and {@code PUT} requests will result in an immediate
     * {@link HttpResponseException} if the HTTP response contains an {@link HttpResponse#isSuccessful() error}
     * {@link HttpResponse#getStatusCode() Status-Code}.
     * <p>
     * If the {@code Message-Body} is being sent using {@link GZipEncoding} and the {@link #setContentType(String)
     * Content-Encoding} header has not already been set, it will automatically be set to a value of {@code gzip}.
     * <p>
     * If the {@code Message-Body} {@link RequestBody#getLength() length} is known, and the {@link #setContentLength(long)
     * Content-Length} header has not already been set, it will automatically be updated.
     * <p>
     * If the {@code Message-Body} {@link RequestBody#getType() type} is known, and the {@link #setContentType(String)
     * Content-Type} header has not already been set, it will automatically be updated.
     * 
     * @return the response from the server
     * @throws HttpResponseException if the HTTP response contains an {@link HttpResponse#isSuccessful() error}
     *                               {@link HttpResponse#getStatusCode() Status-Code}
     * @throws IOException           if any other I/O error occurs
     */
    @Override
    public HttpResponse send() throws IOException {

        if (body != null) {
            connection.setDoOutput(true);

            if (body instanceof GZipEncoding)
                setIfNotSet("Content-Encoding", "gzip");

            if (contentLength < 0 && body.getLength() >= 0)
                contentLength = body.getLength();

            if (body.getType() != null)
                setIfNotSet("Content-Type", body.getType());

            if (contentLength >= 0)
                connection.setFixedLengthStreamingMode(contentLength);
            else
                connection.setChunkedStreamingMode(0);

            try (final OutputStream out = connection.getOutputStream()) {
                body.write(out);
            } catch (final Exception e) {
                connection.disconnect();
                throw e;
            }
        } else
            connection.setFixedLengthStreamingMode(0); // Content-Length = 0

        return super.send();
    }

    /**
     * Sets the {@code Content-Encoding} request header.
     * 
     * @param contentEncoding the value of the {@code Content-Encoding} header
     * @return this {@code HttpRequest} instance
     */
    public HttpRequestWithBody setContentEncoding(final String contentEncoding) {
        if (contentEncoding == null)
            throw new NullPointerException("contentEncoding == null");

        setHeader("Content-Encoding", contentEncoding);
        return this;
    }

    /**
     * Sets the {@code Content-Length} request header and enables {@link HttpURLConnection#setFixedLengthStreamingMode(long)
     * setFixedLengthStreamingMode(long)}.
     * <p>
     * An exception will be thrown if the application attempts to write more data than the indicated {@code Content-Length},
     * or if the application closes the output stream before writing the indicated amount.
     * 
     * @param contentLength the value of the {@code Content-Length} header in bytes
     * @return this {@code HttpRequest} instance
     */
    public HttpRequestWithBody setContentLength(final long contentLength) {
        if (contentLength < 0)
            throw new IllegalArgumentException("contentLength < 0");

        this.contentLength = contentLength;
        return this;
    }

    /**
     * Sets the {@code Content-Type} request header.
     * 
     * @param contentType the value of the {@code Content-Type} header
     * @return this {@code HttpRequest} instance
     */
    public HttpRequestWithBody setContentType(final String contentType) {
        if (contentType == null)
            throw new NullPointerException("contentType == null");

        setHeader("Content-Type", contentType);
        return this;
    }

    /**
     * Sets the {@code Message-Body} to send to the server.
     * 
     * @param body the {@code Message-Body} to send to the server
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public HttpRequestWithBody setBody(final RequestBody body) throws IOException {
        if (body == null)
            throw new NullPointerException("body = null");

        this.body = body;

        return this;
    }

}
