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
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import software.leonov.client.http.guava.RateLimiter;

/**
 * An HTTP request with no {@code Message-Body}: {@code HEAD}, {@code GET}, {@code OPTIONS}, or {@code TRACE}.
 * 
 * @author Zhenya Leonov
 */
public class HttpRequest {

    protected final HttpURLConnection connection;
    protected final RateLimiter       rateLimiter;

    protected HttpRequest(final String method, final URL url, final Proxy proxy, final HostnameVerifier hostnameVerifier, final SSLSocketFactory sslSocketFactory, final RateLimiter rateLimiter) throws IOException {

        if (!url.getProtocol().substring(0, 4).toLowerCase(Locale.US).equals("http"))
            throw new IllegalArgumentException("unsupported protocol: " + url.getProtocol());

        final URLConnection connection = proxy == null ? url.openConnection() : url.openConnection(proxy);

        this.connection = (HttpURLConnection) connection;
        this.connection.setRequestMethod(method);

        if (connection instanceof HttpsURLConnection) {
            if (hostnameVerifier != null)
                ((HttpsURLConnection) connection).setHostnameVerifier(hostnameVerifier);
            if (sslSocketFactory != null)
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
        }

        connection.setDoOutput(false);

        this.rateLimiter = rateLimiter;
    }

    /**
     * Returns the timeout to be used when connecting to the resource referenced by this request or {@code null} if it is
     * not specified.
     * 
     * @return the timeout to be used when connecting to the resource referenced by this request or {@code null} if it is
     *         not specified
     */
    public Duration getConnectTimeout() {
        final int timeout = connection.getConnectTimeout();
        return timeout == 0 ? null : Duration.of(timeout, ChronoUnit.MILLIS);
    }

    /**
     * Returns the value of the {@code If-Modified-Since} HTTP request header or {@code null} if it is not specified.
     * 
     * @return the value of the {@code If-Modified-Since} HTTP request header or {@code null} if it is not specified
     */
    public long getIfModifiedSince() {
        return connection.getIfModifiedSince();
    }

    /**
     * Returns the connection read timeout or {@code null} if it is not specified.
     * 
     * @return the connection read timeout or {@code null} if it is not specified
     */
    public Duration getReadTimeout() {
        final int timeout = connection.getReadTimeout();
        return timeout == 0 ? null : Duration.of(timeout, ChronoUnit.MILLIS);
    }

    /**
     * Returns the value of the named request header or {@code null} if it is not specified.
     * <p>
     * <b>Note:</b> Some request headers like {@code Pragma} or {@code Cache-Control} (not an exhaustive list) are not set
     * until this request is executed, others may not be accessible for security reasons. See
     * {@link #setHeader(String, String)} for more information.
     * 
     * @param name the name of a header field (case-insensitive)
     * @return the value of the named request header or {@code null} if it is not specified
     */
    public String getHeader(final String name) {
        if (name == null)
            throw new NullPointerException("name == null");

        return connection.getRequestProperty(name);
    }

    /**
     * Returns an unmodifiable {@code Map} of the request headers for this request.
     * <p>
     * The keys are strings (which are case-insensitive) that represent the request-header field names, the values are
     * strings that represents the corresponding field values.
     * <p>
     * <b>Note:</b> Some request headers like {@code Pragma}, {@code Cache-Control}, {@code Connection},
     * {@code Proxy-Connection} (not an exhaustive list) are not set until this request is executed, others may not be
     * accessible for security reasons. See {@link #setHeader(String, String)} for more information.
     * 
     * @return an unmodifiable {@code Map} of the general request headers for this request
     */
    public Map<String, String> headers() {
        final Map<String, String> headers = new CaseInsensitiveMap<>(Locale.US);
        connection.getRequestProperties().forEach((name, values) -> headers.put(name, values.get(0)));
        return Collections.unmodifiableMap(headers);
    }

    /**
     * Returns the request method.
     * 
     * @return the HTTP request method
     */
    public String method() {
        return connection.getRequestMethod();
    }

    /**
     * Returns the {@code User-Agent} HTTP request header or {@code null} if it is not specified.
     * <p>
     * If the {@code User-Agent} is not specified when this request is executed it will be set automatically by the
     * underlying {@code URLConnection}.
     * 
     * @return the {@code User-Agent} HTTP request header or {@code null} if it is not specified
     */
    public String getUserAgent() {
        return getHeader("User-Agent");
    }

    /**
     * Returns whether or not automatic HTTP redirection (requests with response code 3xx) is enabled.
     * 
     * @return whether or not automatic HTTP redirection (requests with response code 3xx) is enabled
     */
    public boolean isFollowRedirects() {
        return connection.getInstanceFollowRedirects();
    }

    /**
     * Returns whether or not HTTP caching is enabled.
     * 
     * @return whether or not HTTP caching is enabled
     */
    public boolean isUseCaches() {
        return connection.getUseCaches();
    }

    /**
     * Sends the HTTP request.
     * 
     * @return the response from the server
     * @throws HttpResponseException if the response does not contains a <i>2xx</i> {@link HttpResponse#getStatusCode()
     *                               Status-Code}
     * @throws IOException           if an I/O error occurs
     */
    public HttpResponse send() throws IOException {

        try {
            if (rateLimiter != null)
                rateLimiter.acquire();
            connection.connect();
            final HttpResponse response = new HttpResponse(connection);
            return response;
        } catch (final Exception e) {
            connection.disconnect();
            throw e;
        }
    }

    /**
     * Allows this request to be authenticated using the "Basic" HTTP authentication scheme.
     * <p>
     * <b>Note:</b> According to <a href="https://tools.ietf.org/rfc/rfc7617.txt" target="_blank">RFC-7617</a> the character
     * set to use for this encoding is by default unspecified, as long as it is compatible with US-ASCII. This method uses
     * {@link StandardCharsets#UTF_8 UTF-8}.
     * 
     * @param username the user
     * @param password the password
     * @return this {@code HttpRequest} instance
     */
    public HttpRequest setBasicAuthentication(final String username, final String password) {
        if (username == null)
            throw new NullPointerException("username == null");
        if (password == null)
            throw new NullPointerException("password == null");

        final String data   = username + ":" + password;
        final String base64 = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        return setHeader("Authorization", "Basic " + base64);
    }

    /**
     * Sets a specified timeout to be used when opening a communications link to the resource referenced by this request.
     * <p>
     * If the timeout expires before the connection can be established, a {@link SocketTimeoutException} is raised. A
     * timeout of zero is interpreted as an infinite timeout.
     * 
     * @param timeout the connect timeout
     * @return this {@code HttpRequest} instance
     */
    public HttpRequest setConnectTimeout(final Duration timeout) {
        if (timeout == null)
            throw new NullPointerException("timeout == null");

        connection.setConnectTimeout((int) timeout.toMillis());
        return this;
    }

    /**
     * Enables or disables automatic HTTP redirection (requests with response code 3xx).
     * <p>
     * URL redirection, also known as URL forwarding, is a technique to give a page, a form, or a whole Web application,
     * more than one URL address. HTTP provides a special kind of response, called HTTP redirect, to perform this operation
     * and is used for numerous goals: temporary redirection while site maintenance is ongoing, permanent redirection to
     * keep external links working after a change of the site's architecture, progress pages when uploading a file, and so
     * on.
     * 
     * @param followRedirects a {@code boolean} indicating whether or not to follow HTTP redirects
     * @return this {@code HttpRequest} instance
     */
    public HttpRequest setFollowRedirects(final boolean followRedirects) {
        connection.setInstanceFollowRedirects(followRedirects);
        return this;
    }

    /**
     * Sets the {@code If-Modified-Since} HTTP request header.
     * <p>
     * The {@code If-Modified-Since} request HTTP header makes the request conditional: the server will send back the
     * requested resource, with a {@code 200} status, only if it has been last modified after the given date. If the request
     * has not been modified since, the response will be a {@code 304} without any body; the {@code Last-Modified} response
     * header of a previous request will contain the date of last modification. The {@code If-Modified-Since} can only be
     * used with a {@code GET}, or {@code HEAD} request.
     * 
     * @param millis the number of milliseconds since epoch (January 1, 1970 GMT)
     * @return this {@code HttpRequest} instance
     */
    public HttpRequest setIfModifiedSince(final long millis) {
        connection.setIfModifiedSince(millis);
        return this;
    }

    /**
     * Sets the connection read timeout.
     * <p>
     * A non-zero value specifies the timeout when reading from the resource referenced by this request once a connection is
     * has been established. If the timeout expires before there is data available for read, a
     * {@link SocketTimeoutException} is raised. A timeout of zero is interpreted as an infinite timeout.
     * 
     * @param timeout the read timeout
     * @return this {@code HttpRequest} instance
     */
    public HttpRequest setReadTimeout(final Duration timeout) {
        if (timeout == null)
            throw new NullPointerException("timeout == null");

        connection.setReadTimeout((int) timeout.toMillis());
        return this;
    }

    /**
     * Sets a request header.
     * <p>
     * Staring with Java 7 the following
     * <a href="https://fetch.spec.whatwg.org/#forbidden-header-name" target="_blank">restricted</a> headers cannot be
     * modified or retrieved by the user (changing their values is a no-op) for security reasons:
     * 
     * <pre>
     * Access-Control-Request-Headers
     * Access-Control-Request-Method
     * Connection (<i>close</i> is allowed)
     * Content-Length
     * Content-Transfer-Encoding
     * Host
     * Keep-Alive
     * Origin
     * Trailer
     * Transfer-Encoding
     * Upgrade
     * Via
     * Sec-XXXX (any header starting with <i>Sec-</i>)
     * </pre>
     * 
     * Setting the {@code sun.net.http.allowRestrictedHeaders} system property to {@code true} will revert to previous
     * behavior.
     * 
     * @param name  the header name
     * @param value the header value
     * @return this {@code HttpRequest} instance
     */
    public HttpRequest setHeader(final String name, final String value) {
        if (name == null)
            throw new NullPointerException("name == null");
        if (value == null)
            throw new NullPointerException("value == null");

        connection.setRequestProperty(name, value);
        return this;
    }

    /**
     * Returns the target {@code URL} of this request.
     * 
     * @return the target {@code URL} of this request
     */
    public URL to() {
        return connection.getURL();
    }

    /**
     * Enables or disables HTTP caching.
     * <p>
     * The performance of web sites and applications can be significantly improved by reusing previously fetched resources.
     * Web caches reduce latency and network traffic and thus lessen the time needed to display a representation of a
     * resource. By making use of HTTP caching, Web sites become more responsive.
     * <p>
     * When disabled the {@code Cache-Control} and {@code Pragma} request headers are set to {@code no-cache}.
     * 
     * @param useCaches a boolean indicating whether or not to allow HTTP caching
     * @return this {@code HttpRequest} instance
     */
    public HttpRequest setUseCaches(final boolean useCaches) {
        connection.setUseCaches(useCaches);
        return this;
    }

    /**
     * Sets the {@code User-Agent} HTTP request header.
     * <p>
     * The {@code User-Agent} request header is a characteristic string that lets servers and network peers identify the
     * application, operating system, vendor, and/or version of the requesting user agent.
     * <p>
     * If not set it will be set to the default value the underlying {@code URLConnection} when this request is executed.
     * 
     * @param agent the {@code User-Agent} string
     * @return this {@code HttpRequest} instance
     */
    public HttpRequest setUserAgent(final String agent) {
        if (agent == null)
            throw new NullPointerException("agent == null");

        setHeader("User-Agent", agent);
        return this;
    }

    protected void setIfNotSet(final String name, final String value) {

        if (getHeader(name) == null)
            setHeader(name, value);
    }

}
