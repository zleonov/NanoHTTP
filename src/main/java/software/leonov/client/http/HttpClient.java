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
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * The entry point for making HTTP requests. Instances of this class can be obtained by calling {@link #defaultClient()}
 * or using a {@link #builder() builder}.
 * 
 * @author Zhenya Leonov
 */
// What about https://docs.oracle.com/javase/8/docs/api/java/net/doc-files/net-properties.html#MiscHTTP
public final class HttpClient {

    private final Map<String, List<String>> headers;
    private final Duration connectTimeout;
    private final boolean followRedirects;
    private final Duration readTimeout;
    private final boolean useCaches;
    private final Credentials credentials;
    private final Proxy proxy;

    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;

    private static final HttpClient defaultClient = builder().build();

    private HttpClient(final Proxy proxy, final Map<String, List<String>> headers, final boolean useCaches, final boolean followRedirects, final Duration connectTimeout, final Duration readTimeout, final Credentials credentials,
            final HostnameVerifier hostnameVerifier, final SSLSocketFactory sslSocketFactory) {

        this.proxy = proxy;
        this.headers = headers;
        this.useCaches = useCaches;
        this.followRedirects = followRedirects;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.credentials = credentials;
        this.hostnameVerifier = hostnameVerifier;
        this.sslSocketFactory = sslSocketFactory;
    }

    /**
     * Returns an {@code HttpClient} instance with the following default settings:
     * <p>
     * <ul>
     * <li>Proxy: N/A</li>
     * <li>Accept GZip and Deflate compression in HTTP responses: true</li>
     * <li>Automatic HTTP redirection (requests with response code 3xx): true</li>
     * <li>Enable HTTP caching: true</li>
     * <li>Connect timeout: 60 seconds</li>
     * <li>Read timeout: 60 seconds</li>
     * <li>Basic Authentication: N/A</li>
     * <li>HostnameVerifier: default</li>
     * <li>SSLSocketFactory: default</li>
     * <li>{@code Accept}: default (will be set when requests are executed)</li>
     * <li>{@code User-Agent}: default (will be set when requests are executed)</li>
     * </ul>
     * 
     * @return an {@code HttpClient} instance with default settings
     */
    public static HttpClient defaultClient() {
        return defaultClient;
    }

    /**
     * An <b>HTTP DELETE</b> method deletes the specified resource.
     * 
     * <pre>
     * <table border="1">
     *   <tr><td>Request has body</td><td>May</td></tr>
     *   <tr><td>Successful response has body</td><td>May</td></tr>
     *   <tr><td>Safe</td><td>No</td></tr>
     *   <tr><td>Idempotent</td><td>Yes</td></tr>
     *   <tr><td>Cacheable</td><td>No</td></tr>
     *   <tr><td>Allowed in HTML forms</td><td>No</td></tr>
     * </table>
     * </pre>
     * 
     * <b>Note:</b> While a the latest
     * <a href="https://tools.ietf.org/html/rfc7231#section-4.3.5" target="_blank">RFC-7231</a> allows {@code DELETE}
     * requests to have a message body it <i>has no defined semantics; sending a payload body on a DELETE request might
     * cause some existing implementations to reject the request.</i>
     * 
     * @param url the specified resource
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public HttpRequestWithBody delete(final URL url) throws IOException {
        if (url == null)
            throw new NullPointerException("url == null");
        return setDefaults(new HttpRequestWithBody("DELETE", url, proxy, hostnameVerifier, sslSocketFactory));
    }

    /**
     * An <b>HTTP GET</b> method requests a representation of the specified resource. Requests using {@code GET} should only
     * retrieve data.
     * 
     * <pre>
     * <table border="1">
     *   <tr><td>Request has body</td><td>No</td></tr>
     *   <tr><td>Successful response has body</td><td>Yes</td></tr>
     *   <tr><td>Safe</td><td>Yes</td></tr>
     *   <tr><td>Idempotent</td><td>Yes</td></tr>
     *   <tr><td>Cacheable</td><td>Yes</td></tr>
     *   <tr><td>Allowed in HTML forms</td><td>Yes</td></tr>
     * </table>
     * </pre>
     * 
     * @param url the specified resource
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public HttpRequest get(final URL url) throws IOException {
        if (url == null)
            throw new NullPointerException("url == null");
        return setDefaults(new HttpRequest("GET", url, proxy, hostnameVerifier, sslSocketFactory));
    }

    /**
     * An <b>HTTP HEAD</b> method requests the headers that are returned if the specified resource would be requested with
     * an HTTP {@code GET} method. Such a request can be done before deciding to download a large resource to save
     * bandwidth, for example.
     * <p>
     * A response to a {@code HEAD} method should not have a body. If so, it must be ignored. Even so, entity headers
     * describing the content of the body, like {@code Content-Length} may be included in the response. They don't relate to
     * the body of the {@code HEAD} response, which should be empty, but to the body of similar request using the
     * {@code GET} method would have returned as a response.
     * <p>
     * If the result of a {@code HEAD} request shows that a cached resource after a {@code GET} request is now outdated, the
     * cache is invalidated, even if no {@code GET} request has been made.
     * 
     * <pre>
     * <table border="1">
     *   <tr><td>Request has body</td><td>No</td></tr>
     *   <tr><td>Successful response has body</td><td>No</td></tr>
     *   <tr><td>Safe</td><td>Yes</td></tr>
     *   <tr><td>Idempotent</td><td>Yes</td></tr>
     *   <tr><td>Cacheable</td><td>Yes</td></tr>
     *   <tr><td>Allowed in HTML forms</td><td>No</td></tr>
     * </table>
     * </pre>
     * 
     * @param url the specified resource
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public HttpRequest head(final URL url) throws IOException {
        if (url == null)
            throw new NullPointerException("url == null");
        return setDefaults(new HttpRequest("HEAD", url, proxy, hostnameVerifier, sslSocketFactory));
    }

    /**
     * An <b>HTTP OPTIONS</b> method is used to describe the communication options for the target resource. The client can
     * specify a URL for the {@code OPTIONS} method, or an asterisk (*) to refer to the entire server.
     * 
     * <pre>
     * <table border="1">
     *   <tr><td>Request has body</td><td>No</td></tr>
     *   <tr><td>Successful response has body</td><td>Yes</td></tr>
     *   <tr><td>Safe</td><td>Yes</td></tr>
     *   <tr><td>Idempotent</td><td>Yes</td></tr>
     *   <tr><td>Cacheable</td><td>No</td></tr>
     *   <tr><td>Allowed in HTML forms</td><td>No</td></tr>
     * </table>
     * </pre>
     * 
     * @param url the specified resource
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public HttpRequest options(final URL url) throws IOException {
        if (url == null)
            throw new NullPointerException("url == null");
        return setDefaults(new HttpRequest("OPTIONS", url, proxy, hostnameVerifier, sslSocketFactory));
    }

    /**
     * An <b>HTTP POST</b> method sends data to the server. The type of the body of the request is indicated by the
     * {@code Content-Type} header.
     * <p>
     * The difference between {@code PUT} and {@code POST} is that {@code PUT} is idempotent: calling it once or several
     * times successively has the same effect (that is no side effect), where successive identical {@code POST} may have
     * additional effects, like passing an order several times.
     * <p>
     * A {@code POST} request is typically sent via an HTML form and results in a change on the server. In this case, the
     * content type is selected by putting the adequate string in the {@code enctype} attribute of the {@code <form>}
     * element or the {@code formenctype} attribute of the {@code <input>} or {@code <button>} elements:
     * <p>
     * <ul>
     * <li>{@code application/x-www-form-urlencoded}: the keys and values are encoded in key-value tuples separated by
     * {@code '&'}, with a {@code '='} between the key and the value. Non-alphanumeric characters in both keys and values
     * are percent encoded: this is the reason why this type is not suitable to use with binary data (use
     * {@code multipart/form-data instead})</li>
     * <li>{@code multipart/form-data}: each value is sent as a block of data ("body part"), with a user agent-defined
     * delimiter ("boundary") separating each part. The keys are given in the {@code Content-Disposition} header of each
     * part.</li>
     * <li>{@code text/plain}</li>
     * </ul>
     * <p>
     * When the {@code POST} request is sent via a method other than an HTML form — like via an {@code XMLHttpRequest} — the
     * body can take any type. As described in the HTTP 1.1 specification, {@code POST} designed to allow a uniform method
     * to cover the following functions:
     * <p>
     * <ul>
     * <li>Annotation of existing resources</li>
     * <li>Posting a message to a bulletin board, newsgroup, mailing list, or similar group of articles;</li>
     * <li>Adding a new user through a signup modal;</li>
     * <li>Providing a block of data, such as the result of submitting a form, to a data-handling process;</li>
     * <li>Extending a database through an append operation.</li>
     * </ul>
     * 
     * <pre>
     * <table border="1">
     *   <tr><td>Request has body</td><td>Yes</td></tr>
     *   <tr><td>Successful response has body</td><td>Yes</td></tr>
     *   <tr><td>Safe</td><td>No</td></tr>
     *   <tr><td>Idempotent</td><td>No</td></tr>
     *   <tr><td>Cacheable</td><td>Only if freshness information is included</td></tr>
     *   <tr><td>Allowed in HTML forms</td><td>Yes</td></tr>
     * </table>
     * </pre>
     * 
     * @param url the specified resource
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public HttpRequestWithBody post(final URL url) throws IOException {
        if (url == null)
            throw new NullPointerException("url == null");
        return setDefaults(new HttpRequestWithBody("POST", url, proxy, hostnameVerifier, sslSocketFactory));
    }

    /**
     * An <b>HTTP PUT</b> method creates a new resource or replaces a representation of the target resource with the request
     * payload.
     * <p>
     * The difference between {@code PUT} and {@code POST} is that {@code PUT} is idempotent: calling it once or several
     * times successively has the same effect (that is no side effect), where successive identical {@code POST} may have
     * additional effects, like passing an order several times.
     * 
     * <pre>
     * <table border="1">
     *   <tr><td>Request has body</td><td>Yes</td></tr>
     *   <tr><td>Successful response has body</td><td>No</td></tr>
     *   <tr><td>Safe</td><td>No</td></tr>
     *   <tr><td>Idempotent</td><td>Yes</td></tr>
     *   <tr><td>Cacheable</td><td>No</td></tr>
     *   <tr><td>Allowed in HTML forms</td><td>No</td></tr>
     * </table>
     * </pre>
     * 
     * @param url the specified resource
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public HttpRequestWithBody put(final URL url) throws IOException {
        if (url == null)
            throw new NullPointerException("url == null");
        return setDefaults(new HttpRequestWithBody("PUT", url, proxy, hostnameVerifier, sslSocketFactory));
    }

    /**
     * An <b>HTTP TRACE</b> method performs a message loop-back test along the path to the target resource, providing a
     * useful debugging mechanism.
     * <p>
     * The final recipient of the request should reflect the message received, excluding some fields described below, back
     * to the client as the message body of a {@code 200} (OK) response with a {@code Content-Type} of message/http. The
     * final recipient is either the origin server or the first server to receive a {@code Max-Forwards} value of 0 in the
     * request.
     * 
     * <pre>
     * <table border="1">
     *   <tr><td>Request has body</td><td>No</td></tr>
     *   <tr><td>Successful response has body</td><td>No</td></tr>
     *   <tr><td>Safe</td><td>No</td></tr>
     *   <tr><td>Idempotent</td><td>Yes</td></tr>
     *   <tr><td>Cacheable</td><td>No</td></tr>
     *   <tr><td>Allowed in HTML forms</td><td>No</td></tr>
     * </table>
     * </pre>
     * 
     * @param url the specified resource
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public HttpRequest trace(final URL url) throws IOException {
        if (url == null)
            throw new NullPointerException("url == null");
        return setDefaults(new HttpRequest("TRACE", url, proxy, hostnameVerifier, sslSocketFactory));
    }

    private <T extends HttpRequest> T setDefaults(final T request) {
        headers.forEach((name, values) -> values.forEach(value -> request.setHeader(name, value)));

        request.setUseCaches(useCaches).setReadTimeout(readTimeout).setFollowRedirects(followRedirects).setConnectTimeout(connectTimeout);

        if (credentials != null)
            request.setBasicAuthentication(credentials.username(), credentials.password());

        return request;
    }

    private static final class Credentials {

        private final String username;
        private final String password;

        public Credentials(final String username, final String password) {
            this.username = username;
            this.password = password;
        }

        public String username() {
            return username;
        }

        public String password() {
            return password;
        }

    }

    /**
     * A builder of {@code HttpClient} instances.
     * 
     * @author Zhenya Leonov
     */
    public static final class Builder {

        private final Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        private boolean followRedirects = true;
        private boolean useCaches = true;

        private Duration connectTimeout = Duration.ofSeconds(60);
        private Duration readTimeout = Duration.ofSeconds(60);

        private Credentials credentials = null;

        private HostnameVerifier hostnameVerifier = null;
        private SSLSocketFactory sslSocketFactory = null;

        private Proxy proxy = null;

        private Builder() {
        };

        /**
         * Returns a new {@code HttpClient} configured by this builder.
         * 
         * @return a new {@code HttpClient} configured by this builder
         */
        public HttpClient build() {
            return new HttpClient(proxy, headers, useCaches, followRedirects, connectTimeout, readTimeout, credentials, hostnameVerifier, sslSocketFactory);
        }

        /**
         * Turns off all SSL validation by trusting all X.509 certificates and hostnames in all HTTPS requests generated by this
         * {@code HttpClient}.
         * <p>
         * <b>Warning:</b> This method is provided for testing and convenience. It should <b>never</b> be called in a production
         * environment.
         * 
         * @return this {@code Builder} instance
         * @throws GeneralSecurityException if an error occurs
         */
        public Builder disableSSLValidation() throws GeneralSecurityException {

            final SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, new TrustManager[] { new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

            } }, null);

            setHostnameVerifier((hostname, session) -> {
                return true;
            }).setSSLSocketFactory(sc.getSocketFactory());

            return this;
        }

        /**
         * Allows all requests generated by this {@code HttpClient} to be authenticated using the "Basic" HTTP authentication
         * scheme.
         * <p>
         * <b>Note:</b> According to <a href="https://tools.ietf.org/rfc/rfc7617.txt" target="_blank">RFC-7617</a> the character
         * set to use for this encoding is by default unspecified, as long as it is compatible with US-ASCII. This method uses
         * {@link StandardCharsets#UTF_8 UTF-8}.
         * 
         * @param username the user
         * @param password the password
         * @return this {@code Builder} instance
         */
        public Builder setBasicAuthentication(final String username, final String password) {
            if (username == null)
                throw new NullPointerException("username == null");
            if (password == null)
                throw new NullPointerException("password == null");

            credentials = new Credentials(username, password);

            return this;
        }

        /**
         * Sets the default timeout to be used when connecting to resources referenced by requests generated by this
         * {@code HttpClient}.
         * <p>
         * If the timeout expires before the connection can be established, a {@link SocketTimeoutException} is raised. A
         * timeout of zero is interpreted as an infinite timeout.
         * 
         * @param timeout the connect timeout
         * @return this {@code Builder} instance
         */
        public Builder setConnectTimeout(final Duration connectTimeout) {
            if (connectTimeout == null)
                throw new NullPointerException("connectTimeout == null");

            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Enables or disables default automatic HTTP redirection (requests with response code 3xx) for all HTTPS requests
         * generated by this {@code HttpClient}.
         * <p>
         * URL redirection, also known as URL forwarding, is a technique to give a page, a form, or a whole Web application,
         * more than one URL address. HTTP provides a special kind of response, called HTTP redirect, to perform this operation
         * and is used for numerous goals: temporary redirection while site maintenance is ongoing, permanent redirection to
         * keep external links working after a change of the site's architecture, progress pages when uploading a file, and so
         * on.
         * 
         * @param followRedirects a {@code boolean} indicating whether or not to follow HTTP redirects
         * @return this {@code Builder} instance
         */
        public Builder setFollowRedirects(final boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        /**
         * Sets the default connection read timeout for all requests generated by this {@code HttpClient}.
         * <p>
         * A non-zero value specifies the timeout when reading from the resource referenced by this request once a connection is
         * has been established. If the timeout expires before there is data available for read, a
         * {@link SocketTimeoutException} is raised. A timeout of zero is interpreted as an infinite timeout.
         * 
         * @param timeout the read timeout
         * @return this {@code Builder} instance
         */
        public Builder setReadTimeout(final Duration readTimeout) {
            if (readTimeout == null)
                throw new NullPointerException("readTimeout == null");

            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Sets a default request header for all requests generated by this {@code HttpClient}.
         * <p>
         * Staring with Java 7 the following
         * <a href="https://fetch.spec.whatwg.org/#forbidden-header-name" target="_blank">restricted</a> headers cannot be
         * modified by the user (changing their values is a no-op) for security reasons:
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
         * @return this {@code Builder} instance
         */
        public Builder setHeader(final String name, final String value) {
            if (name == null)
                throw new NullPointerException("name == null");
            if (value == null)
                throw new NullPointerException("value == null");

            final List<String> values = new ArrayList<>(1);
            values.add(value);
            headers.put(name, values);
            return this;
        }

        /**
         * Enables or disables the default HTTP caching behavior for all requests generated by this {@code HttpClient}.
         * <p>
         * The performance of web sites and applications can be significantly improved by reusing previously fetched resources.
         * Web caches reduce latency and network traffic and thus lessen the time needed to display a representation of a
         * resource. By making use of HTTP caching, Web sites become more responsive.
         * <p>
         * When disabled the {@code Cache-Control} and {@code Pragma} request headers are set to {@code no-cache}.
         * 
         * @param useCaches a boolean indicating whether or not to allow HTTP caching
         * @return this {@code Builder} instance
         */
        public Builder setUseCaches(final boolean useCaches) {
            this.useCaches = useCaches;
            return this;
        }

        /**
         * Sets the default {@code User-Agent} HTTP request header for all requests generated by this {@code HttpClient}.
         * <p>
         * The {@code User-Agent} request header is a characteristic string that lets servers and network peers identify the
         * application, operating system, vendor, and/or version of the requesting user agent.
         * 
         * @param agent the {@code User-Agent} string
         * @return this {@code Builder} instance
         */
        public Builder setUserAgent(final String agent) {
            if (agent == null)
                throw new NullPointerException("timeout == null");

            return setHeader("User-Agent", agent);
        }

        /**
         * Sets the default {@code HostnameVerifier} to use for all HTTPS requests generated by this {@code HttpClient}.
         * 
         * @param HostnameVerifier the {@code HostnameVerifier} to use
         * @return this {@code Builder} instance
         */
        public Builder setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
            if (hostnameVerifier == null)
                throw new NullPointerException("hostnameVerifier == null");

            this.hostnameVerifier = hostnameVerifier;
            return this;

        }

        /**
         * Sets the {@code Proxy} to use for all requests generated by this {@code HttpClient}.
         * 
         * @param proxy the {@code Proxy} to use
         * 
         * @return this {@code Builder} instance
         */
        public Builder setProxy(final Proxy proxy) {
            if (proxy == null)
                throw new NullPointerException("proxy == null");

            this.proxy = proxy;
            return this;
        }

        /**
         * Sets the default {@code SSLSocketFactory} to use for all HTTPS requests generated by this {@code HttpClient}.
         * 
         * @param sslSocketFactory the {@code SSLSocketFactory} to use
         * @return this {@code Builder} instance
         */
        public Builder setSSLSocketFactory(final SSLSocketFactory sslSocketFactory) {
            if (sslSocketFactory == null)
                throw new NullPointerException("sslSocketFactory == null");

            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

    }

    /**
     * Returns a builder that configures and creates {@code HttpClient} instances. The builder is initialized with the
     * {@link HttpClient#defaultClient() default} settings.
     * 
     * @return a builder that configures and creates {@code HttpClient} instances
     */
    public static Builder builder() {
        return new Builder().setHeader("Accept-Encoding", "gzip, deflate");
    }

}
