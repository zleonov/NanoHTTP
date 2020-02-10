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

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

/**
 * An HTTP response.
 * 
 * @author Zhenya Leonov
 */
public class HttpResponse implements AutoCloseable {

    private final HttpURLConnection connection;

    private final String encoding;
    private final String contentType;
    private final String reason;
    private final String statusLine;
    private final long length;
    private final long date;
    private final long expiration;
    private final long ifModifiedSince;
    private final boolean hasBody;
    private final ResponseBody body;
    private final Map<String, List<String>> headers;
    private final int statusCode;
    private final URL from;
    private final MediaType mediaType;

    private Charset charset = StandardCharsets.ISO_8859_1; // default charset

    HttpResponse(final HttpURLConnection connection) throws IOException {
        if (connection == null)
            throw new NullPointerException("connection == null");

        this.connection = connection;

        statusCode = connection.getResponseCode();

        length = connection.getContentLengthLong();
        statusLine = connection.getHeaderField(0);
        reason = connection.getResponseMessage();
        contentType = connection.getContentType();
        encoding = connection.getContentEncoding();
        from = connection.getURL();
        mediaType = MediaType.tryParse(contentType);

        if (mediaType != null && mediaType.charset() != null)
            charset = mediaType.charset();

        if (statusCode < 200 || statusCode >= 300)
            throw new HttpResponseException(getStatusLine()).setServerResponse(getErrorMessage()).setHeaders(headers()).setStatusCode(getStatusCode()).from(from());

        final Map<String, List<String>> headers = new TreeMap<>(Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
        connection.getHeaderFields().forEach((name, values) -> headers.put(name, values));
        this.headers = Collections.unmodifiableMap(headers);

        date = connection.getDate();
        expiration = connection.getExpiration();
        ifModifiedSince = connection.getIfModifiedSince();

        hasBody = connection.getRequestMethod().equals("HEAD") || statusCode < 200 || statusCode == HTTP_NO_CONTENT || statusCode == HTTP_NOT_MODIFIED ? false : true;

        body = hasBody ? new ResponseBody() {

            @Override
            public InputStream getInputStream() throws IOException {
                return unzip(connection.getInputStream());
            }

            @Override
            public String asString() throws IOException {
                return new String(toByteArray(), charset);
            }

            @Override
            public byte[] toByteArray() throws IOException {
                return ByteStream.toByteArray(getInputStream());
            }
        } : null;

    }

    /**
     * Closes any open input or error streams to the server.
     */
    @Override
    public void close() {

        InputStream in = null;
        try {
            in = connection.getInputStream();
            if (in != null)
                ByteStream.discard(in);
        } catch (final IOException e) {
        } finally {
            ByteStream.closeQuietly(in);
        }

        try {
            in = connection.getErrorStream();
            if (in != null)
                ByteStream.discard(in);
        } catch (final IOException e) {
        } finally {
            ByteStream.closeQuietly(in);
        }

    }

    /**
     * Disconnects the underlying {@code HttpURLConnection}. Calling this method indicates that other requests to the server
     * are unlikely in the near future, meaning this connection will not be reused, and all resources will be closed and/or
     * garbage collected.
     * 
     * @return this HttpResponse instance
     */
    public HttpResponse disconnect() {
        connection.disconnect();
        return this;
    }

    /**
     * Returns the request {@code URL} that initiated this {@link HttpRequest}.
     * 
     * @return the request {@code URL} that initiated this {@link HttpRequest}
     */
    public URL from() {
        return from;
    }

    /**
     * Returns the charset specified in the {@code Content-Type} header field, or {@link StandardCharsets#ISO_8859_1
     * ISO_8859_1} if it is unspecified, unsupported, or cannot be discerned.
     * <p>
     * As stated in <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.5" target="_blank">RFC-7231</a>: <i>In
     * practice, resource owners do not always properly configure their origin server to provide the correct Content-Type
     * for a given representation.</i> Users may wish to call {@link #setContentCharset(Charset)} to override the charset.
     * 
     * @return the charset specified in the {@code Content-Type} header field, or {@link StandardCharsets#ISO_8859_1
     *         ISO_8859_1} if it is unspecified, unsupported, or cannot be discerned
     */
    public Charset getContentCharset() {
        return charset;
    }

    /**
     * Returns the value of the {@code Content-Encoding} header field or {@code null} if it is not known.
     * 
     * @return the value of the {@code Content-Encoding} header field or {@code null} if it is not known
     */
    public String getContentEncoding() {
        return encoding;
    }

    /**
     * Returns the value of the {@code Content-Length} header field or -1 if it is not known or cannot be discerned.
     * 
     * @return the value of the {@code Content-Length} header field or -1 if it is not known or cannot be discerned
     */
    public long getContentLength() {
        return length;
    }

    /**
     * Returns the value of the {@code Content-Type} header field or {@code null} if it is not known.
     * <p>
     * Use {@link #getMediaType()} to parse the {@code Content-Type} header.
     * 
     * @return the value of the {@code Content-Type} header field or {@code null} if it is not known
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns a {@code MediaType} parsed from the {@link #getContentType() Content-Type} header or {@code null} if it is
     * not known or cannot be parsed.
     * <p>
     * If this method returns {@code null} but the {@code Content-Type} header exists the value can still be retrieved by
     * calling the {@link #getContentType()} method.
     * 
     * @return a {@code MediaType} parsed from the {@link #getContentType() Content-Type} header or {@code null} if it is
     *         not known or cannot be parsed
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * Returns the value of the {@code Date} header field or 0 if it is not known.
     * 
     * @return the value of the {@code Date} header field or 0 if it is not known
     */
    public long getDate() {
        return date;
    }

    /**
     * Returns the value of the {@code Expires} header field or 0 if it is not known or already expired.
     * <p>
     * Returns the value of the {@code Expires} header field or 0 if it is not known or already expired
     * 
     * @return
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * Returns the value of the {@code If-Modified-Since} header field or 0 to indicate that fetching must always occur.
     * 
     * @return the value of the {@code If-Modified-Since} header field or 0 to indicate that fetching must always occur
     */
    public long getIfModifiedSince() {
        return ifModifiedSince;
    }

    /**
     * Returns the {@code Message-Body} sent by the server or {@code null} if this response {@link #hasBody() does not have}
     * a {@code Message-Body}.
     * <p>
     * <b>Note:</b> Whether or not a {@code Message-Body} is buffered in the response is implementation dependent. Unless
     * otherwise stated, it should be assumed that the {@code ResponseBody} is <i>one-shot</i> stream backed by an active
     * connection to the server, and may be consumed only <b>once</b> by calling the the {@link ResponseBody#asString()},
     * {@link ResponseBody#toByteArray()}, or {@link ResponseBody#getInputStream()} methods.
     * <p>
     * The response body <i>should</i> be consumed in it's entirety to allow the underlying {@code Connection} to be reused,
     * assuming <i>keep-alive</i> is on. All underlying streams will be closed when this response is {@link #close()
     * closed}.
     * 
     * @return the {@code Message-Body} sent by the server or {@code null} if this response {@link #hasBody() does not have}
     *         a {@code Message-Body}
     * @throws IOException if any other I/O error occurs
     */
    public ResponseBody getBody() throws IOException {
        return body;
    }

    /**
     * Returns the {@code Reason-Phrase}, if any, parsed alongside the {@link #getStatusCode() Status-Code} from the
     * {@link #getStatusLine() Status-Line} or {@code null} if it could be discerned (the result was not valid HTTP).
     * 
     * @return the {@code Reason-Phrase}, if any, parsed alongside the {@link #getStatusCode() Status-Code} from the
     *         {@link #getStatusLine() Status-Line} or {@code null} if it could be discerned (the result was not valid HTTP)
     */
    public String getReasonPhrase() {
        return reason;
    }

    /**
     * Returns the value of the named response header or {@code null} if it is not specified.
     * <p>
     * If the header field was defined multiple times, only the last value is returned.
     *
     * @param name the name of a header field (case-insensitive)
     * @return the value of the named response header or {@code null} if it is not specified
     */
    public String getHeader(final String name) {
        if (name == null)
            throw new NullPointerException("name == null");

        return connection.getHeaderField(name);
    }

    /**
     * Returns an unmodifiable {@code Map} of the response headers sent by the server.
     * <p>
     * The keys are strings (which are case-insensitive) that represent the response-header field names, the values are
     * unmodifiable {@code List}s of strings that represents the corresponding field values. The order of the response
     * headers is <b>not</b> maintained.
     * 
     * @return an unmodifiable {@code Map} of the response headers sent by the server
     */
    public Map<String, List<String>> headers() {
        return headers;
    }

    /**
     * Returns the {@code Status-Code} parsed from the HTTP response or -1 if no code can be discerned (the result was not
     * valid HTTP).
     * <p>
     * The first digit of the {@code Status-Code} defines the class of response. The last two digits do not have any
     * categorization role. There are 5 values for the first digit:
     * 
     * <pre>
     * - 1xx: Informational - Request received, continuing process
     *  
     * - 2xx: Success - The action was successfully received, understood, and accepted
     * 
     * - 3xx: Redirection - Further action must be taken in order to complete the request
     * 
     * - 4xx: Client Error - The request contains bad syntax or cannot be fulfilled
     * 
     * - 5xx: Server Error - The server failed to fulfill an apparently valid request
     * </pre>
     * 
     * @return the {@code Status-Code} parsed from the HTTP response or -1 if no code can be discerned (the result was not
     *         valid HTTP)
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the {@code Status-Line} parsed from the HTTP response or {@code null} if it cannot be discerned (the result
     * was not valid HTTP).
     * 
     * @return the {@code Status-Line} parsed from the HTTP response or {@code null} if it cannot be discerned (the result
     *         was not valid HTTP)
     */
    public String getStatusLine() {
        return statusLine;
    }

    /**
     * Returns {@code true} if this response contains a {@code Message-Body} according to
     * <a href="https://tools.ietf.org/html/rfc7230#section-3.3" target="_blank">RFC-7230</a>, else {@code false}:
     * 
     * <pre>
     * Responses to the HEAD request method (Section 4.3.2
     * of [RFC7231]) never include a message body because the associated
     * response header fields (e.g., Transfer-Encoding, Content-Length,
     * etc.), if present, indicate only what their values would have been if
     * the request method had been GET (Section 4.3.1 of [RFC7231]). 2xx
     * (Successful) responses to a CONNECT request method (Section 4.3.6 of
     * [RFC7231]) switch to tunnel mode instead of having a message body.
     * All 1xx (Informational), 204 (No Content), and 304 (Not Modified)
     * responses do not include a message body.  All other responses do
     * include a message body, although the body might be of zero length.
     * </pre>
     * 
     * @return {@code true} if this response contains a {@code Message-Body} according to
     *         <a href="https://tools.ietf.org/html/rfc7230#section-3.3" target="_blank">RFC-7230</a>, else {@code false}
     */
    public boolean hasBody() {
        return hasBody;
    }

    /**
     * Override {@link #getContentCharset() Content-Type} charset returned by the server. Calling this method will ensure
     * {@link #getBody() getMessageBody()}{@link ResponseBody#asString() .asString()} will use the specified charset to
     * parse the {@code Message-Body}.
     * <p>
     * As stated in <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.5" target="_blank">RFC-7231</a>: <i>In
     * practice, resource owners do not always properly configure their origin server to provide the correct Content-Type
     * for a given representation.</i>
     * 
     * @param charset the charset to use
     * @return this {@code HttpResponse} instance
     */
    public HttpResponse setContentCharset(final Charset charset) {
        if (charset == null)
            throw new NullPointerException("charset == null");

        this.charset = charset;
        return this;
    }

    private String getErrorMessage() throws IOException {
        final InputStream in = connection.getErrorStream();
        return in == null ? null : new String(ByteStream.toByteArray(unzip(in)), getContentCharset());
    }

    private InputStream unzip(final InputStream in) throws IOException {
        if (in == null)
            throw new NullPointerException("in == null");

        if ("gzip".equalsIgnoreCase(getContentEncoding()) || "x-gzip".equalsIgnoreCase(getContentEncoding()))
            return new GZIPInputStream(in);

        if ("deflate".equalsIgnoreCase(getContentEncoding()))
            return new DeflaterInputStream(in);

        return in;
    }

}
