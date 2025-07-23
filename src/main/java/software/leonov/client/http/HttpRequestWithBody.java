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
import java.nio.file.Path;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * An HTTP request which contains a {@code Message-Body}: {@code DELETE}, {@code POST}, or {@code PUT}.
 * 
 * @author Zhenya Leonov
 */
public final class HttpRequestWithBody extends HttpRequest {

    private RequestBody body   = null;
    private long        length = -1;

    HttpRequestWithBody(final String method, final URL url, final Proxy proxy, final HostnameVerifier hostnameVerifier, final SSLSocketFactory sslSocketFactory, final RateLimiter rateLimiter, final HttpRequestInterceptor interceptor)
            throws IOException {
        super(method, url, proxy, hostnameVerifier, sslSocketFactory, rateLimiter, interceptor);
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
     * Returns the value of the {@code Content-Length} request header or -1 if it is not specified.
     * 
     * @param the value of the {@code Content-Length} header or -1 if it is not specified
     * @return this {@code HttpRequest} instance
     */
    public long getContentLength() {
        return length;
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
     * If the {@code Message-Body} {@link RequestBody#getContentEncoding() Content-Encoding} is known, and the
     * {@code Content-Encoding} header has not already been {@link #setContentEncoding(String) set}, it will automatically
     * be updated.
     * <p>
     * If the {@code Message-Body} {@link RequestBody#length() Content-Length} is known, and the {@code Content-Length}
     * header has not already been {@link #setContentLength(long) set}, it will automatically be updated.
     * <p>
     * If the {@code Message-Body} {@link RequestBody#getContentType() Content-Type} is known, and the {@code Content-Type}
     * header has not already been {@link #setContentType(String) set}, it will automatically be updated.
     * 
     * @return the response from the server
     * @throws HttpResponseException if the response does not contains a <i>2xx</i> {@link HttpResponse#getStatusCode()
     *                               Status-Code}
     * @throws IOException           if any other I/O error occurs
     */
    @Override
    public HttpResponse send() throws IOException {

        final HttpURLConnection newConnection = createConnection(connection, getRequestMethod(), getURL(), proxy, connection instanceof HttpsURLConnection ? ((HttpsURLConnection) connection).getHostnameVerifier() : null,
                connection instanceof HttpsURLConnection ? ((HttpsURLConnection) connection).getSSLSocketFactory() : null);

        try {
            if (body != null) {
                connection.setDoOutput(true);

                if (body.getContentEncoding() != null)
                    setIfNotSet("Content-Encoding", body.getContentEncoding());

                if (body.getContentType() != null)
                    setIfNotSet("Content-Type", body.getContentType());

                if (length < 0 && body.length() >= 0)
                    length = body.length();

                if (length >= 0)
                    connection.setFixedLengthStreamingMode(length);
                else
                    connection.setChunkedStreamingMode(0);

                super.connect();

                try (final OutputStream out = connection.getOutputStream()) {
                    body.write(out);
                } catch (final Exception e) {
                    connection.disconnect();
                    throw e;
                }

                return new HttpResponse(connection);
            } else {
                connection.setFixedLengthStreamingMode(0); // Content-Length = 0
                return super.send();
            }
        } finally {
            this.connection = newConnection;
        }
    }

    /**
     * Sets the {@code Content-Encoding} request header.
     * 
     * @param encoding the value of the {@code Content-Encoding} header
     * @return this {@code HttpRequest} instance
     */
    public HttpRequestWithBody setContentEncoding(final String encoding) {
        if (encoding == null)
            throw new NullPointerException("encoding == null");

        setHeader("Content-Encoding", encoding);
        return this;
    }

    /**
     * Sets the {@code Content-Length} request header and enables {@link HttpURLConnection#setFixedLengthStreamingMode(long)
     * setFixedLengthStreamingMode(long)}.
     * <p>
     * An exception will be thrown if the application attempts to write more data than the indicated {@code Content-Length},
     * or if the application closes the output stream before writing the indicated amount.
     * 
     * @param length the value of the {@code Content-Length} header in bytes
     * @return this {@code HttpRequest} instance
     */
    public HttpRequestWithBody setContentLength(final long length) {
        if (length < 0)
            throw new IllegalArgumentException("length < 0");

        this.length = length;
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

    /**
     * Sends the specified text encoded in the UTF-8 charset to the server.
     * <p>
     * Shorthand for {@code setContentType(contentType).setBody(ByteArrayBody.encode(text)).send()}.
     * 
     * @param text        the text to send
     * @param contentType the value of the {@code Content-Type} header
     * @return the response from the server
     * @throws HttpResponseException if the response does not contains a <i>2xx</i> {@link HttpResponse#getStatusCode()
     *                               Status-Code}
     * @throws IOException           if any other I/O error occurs
     */
    public HttpResponse text(final String text, final String contentType) throws IOException {
        if (text == null)
            throw new NullPointerException("text = null");
        if (contentType == null)
            throw new NullPointerException("contentType = null");

        return setContentType(contentType).setBody(ByteArrayBody.encode(text)).send();
    }

    /**
     * Sends the specified file to the server. If the file size is greater than 1MB it will be sent using
     * {@link GZipEncoding}.
     * <p>
     * Shorthand for {@code setContentType(contentType).setBody(new FileBody(file)).send()}
     * 
     * @param file        the file to send
     * @param contentType the value of the {@code Content-Type} header
     * @return the response from the server
     * @throws HttpResponseException if the response does not contains a <i>2xx</i> {@link HttpResponse#getStatusCode()
     *                               Status-Code}
     * @throws IOException           if any other I/O error occurs
     */
    public HttpResponse file(final Path file, final String contentType) throws IOException {
        if (file == null)
            throw new NullPointerException("file = null");
        if (contentType == null)
            throw new NullPointerException("contentType = null");

        setContentType(contentType);

        final FileBody filebody = new FileBody(file);

        if (filebody.length() > 1024)
            setBody(new GZipEncoding(filebody));
        else
            setBody(filebody);

        return send();

        // return setContentType(contentType).setBody(fileBody.length() > 1024 ? new GZipEncoding(fileBody) : fileBody).send();
    }

}
