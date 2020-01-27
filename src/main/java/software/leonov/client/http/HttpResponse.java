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
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import javax.swing.text.Utilities;

/**
 * An HTTP response.
 * 
 * @author Zhenya Leonov
 */
public class HttpResponse implements AutoCloseable {

    private static final Logger logger = Logger.getLogger(Utilities.class.getName());

    private static final Pattern CHARSET = Pattern.compile("(?i)charset=\\s*\"?([^\\s;\"]*)");

    private Charset charset = StandardCharsets.ISO_8859_1;

    private final HttpURLConnection connection;

    private final String contentEncoding;
    private final String contentType;
    private final String reasonPhrase;
    private final String statusLine;

    private final long contentLength;
    private final long date;
    private final long expiration;
    private final long ifModifiedSince;

    private final ResponseBody responseBody;

    private final Map<String, List<String>> responseHeaders;

    private final int statusCode;

    private final URL url;

    HttpResponse(final HttpURLConnection connection) throws IOException {
        if (connection == null)
            throw new NullPointerException("connection == null");

        this.connection = connection;

        statusCode = connection.getResponseCode();
        contentLength = connection.getContentLengthLong();
        statusLine = connection.getHeaderField(0);
        reasonPhrase = connection.getResponseMessage();
        contentType = connection.getContentType();
        contentEncoding = connection.getContentEncoding();
        url = connection.getURL();

        final Map<String, List<String>> headers = new TreeMap<>(Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
        connection.getHeaderFields().forEach((name, values) -> headers.put(name, values));
        responseHeaders = Collections.unmodifiableMap(headers);

        date = connection.getDate();
        expiration = connection.getExpiration();
        ifModifiedSince = connection.getIfModifiedSince();

        if (contentType != null) {
            final Matcher matcher = CHARSET.matcher(contentType);
            if (matcher.find())
                try {
                    charset = Charset.forName(matcher.group(1));
                } catch (final IllegalCharsetNameException | UnsupportedCharsetException e) {
                    logger.log(Level.WARNING, e, () -> String.format("[%s] Unable to parse the Content-Type charset: %s", Thread.currentThread().getName(), contentType));
                }
        }

        if (!isSuccessful() && connection.getRequestMethod().equals("PUT") || connection.getRequestMethod().equals("POST") || connection.getRequestMethod().equals("DELETE"))
            throw new HttpException(getStatusLine()).setErrorMessage(getErrorMessage()).setResponseHeaders(getHeaders()).setStatusCode(getStatusCode()).setURL(from());

        responseBody = isSuccessful() && hasBody() ? new AbstractResponseBody(getContentCharset()) {

            @Override
            public InputStream getInputStream() throws IOException {
                return unzip(connection.getInputStream());
            }
        } : null;

    }

    /**
     * Closes any open input or error streams to the server.
     */
    @Override
    public void close() {

        // InputStream in = null;

        try {
            closeQuietly(connection.getInputStream());
        } catch (final IOException e) {
            // logger.log(Level.WARNING, e, () -> String.format("[%s] Connection.getInputStream() threw an IOException:",
            // Thread.currentThread().getName()));
        }

        closeQuietly(connection.getErrorStream());
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
        return url;
    }

    /**
     * Returns the charset specified in the {@code Content-Type} header field, or {@link StandardCharsets#ISO_8859_1
     * ISO_8859_1} if it is unspecified, unsupported, or cannot be discerned.
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
        return contentEncoding;
    }

    /**
     * Returns the value of the {@code Content-Length} header field or -1 if it is not known or cannot be discerned.
     * 
     * @return the value of the {@code Content-Length} header field or -1 if it is not known or cannot be discerned
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * Returns the value of the {@code Content-Type} header field or {@code null} if it is not known.
     * 
     * @return the value of the {@code Content-Type} header field or {@code null} if it is not known
     */
    public String getContentType() {
        return contentType;
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
     * Returns the error message when a request failed but the server sent useful data nonetheless or {@code null} if no
     * error occurred or no error data was sent.
     * 
     * @return the error message when a request failed but the server sent useful data nonetheless or {@code null} if no
     *         error occurred or no error data was sent
     * @throws IOException if an I/O error occurs
     */
    public String getErrorMessage() throws IOException {
        final InputStream in = connection.getErrorStream();
        return in == null ? null : new String(AbstractResponseBody.toByteArray(unzip(in)), getContentCharset());
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
     * @throws IOException   if an I/O error occurs
     * @throws HttpException if an error occurs when communicating with the remote resource
     */
    public ResponseBody getBody() throws HttpException, IOException {
        if (isSuccessful())
            return responseBody;
        else
            throw new HttpException(getStatusLine()).setErrorMessage(getErrorMessage()).setResponseHeaders(getHeaders()).setStatusCode(getStatusCode()).setURL(from());
    }

    /**
     * Returns the {@code Reason-Phrase}, if any, parsed alongside the {@link #getStatusCode() Status-Code} from the
     * {@link #getStatusLine() Status-Line} or {@code null} if it could be discerned (the result was not valid HTTP).
     * 
     * @return the {@code Reason-Phrase}, if any, parsed alongside the {@link #getStatusCode() Status-Code} from the
     *         {@link #getStatusLine() Status-Line} or {@code null} if it could be discerned (the result was not valid HTTP)
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    /**
     * Returns the value of the named header field.
     * <p>
     * If the header field was defined multiple times, only the last value is returned.
     *
     * @param name the name of a header field (case-insensitive)
     * @return the value of the named header field or {@code null} if there is no such field in the response headers
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
    public Map<String, List<String>> getHeaders() {
        return responseHeaders;
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
        return connection.getRequestMethod().equals("HEAD") || statusCode < 200 || statusCode == HTTP_NO_CONTENT || statusCode == HTTP_NOT_MODIFIED ? false : true;
    }

    /**
     * Returns {@code true} if the response contains a 2xx {@link #getStatusCode() Status-Code}, else {@code false}.
     * 
     * @return {@code true} if the response contains a 2xx {@link #getStatusCode() Status-Code}, else {@code false}
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
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

    private InputStream unzip(final InputStream in) throws IOException {
        if ("gzip".equals(getContentEncoding()))
            return new GZIPInputStream(in);
        else if ("x-gzip".equals(getContentEncoding()))
            return new GZIPInputStream(in);
        else if ("deflate".equals(getContentEncoding()))
            return new DeflaterInputStream(in);
        else
            return in;
    }

    private static void closeQuietly(final InputStream in) {
        try {
            if (in != null)
                in.close();
        } catch (final IOException e) {
            logger.log(Level.WARNING, e, () -> String.format("[%s] InputStream.close() threw an IOException", Thread.currentThread().getName()));
        }
    }

}
