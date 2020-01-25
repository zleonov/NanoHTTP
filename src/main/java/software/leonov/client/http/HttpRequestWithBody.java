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
    private long length = -1;

    HttpRequestWithBody(final String method, final URL url, final Proxy proxy, final HostnameVerifier hostnameVerifier, final SSLSocketFactory sslSocketFactory) throws IOException {
        super(method, url, proxy, hostnameVerifier, sslSocketFactory);
    }

    /**
     * Sends the HTTP request.
     * 
     * @return the response from the server
     * @throws IOException if an I/O error occurs
     */
    @Override
    public HttpResponse send() throws IOException {

        if (isAcceptGZipEncoding())
            _setAcceptGZipEncoding();

        if (body != null) {
            connection.setDoOutput(true);

            if (length >= 0)
                connection.setFixedLengthStreamingMode(length);
            else
                connection.setChunkedStreamingMode(0);

            try (final OutputStream out = connection.getOutputStream()) {
                body.write(out);
            } catch (final Exception e) {
                connection.disconnect();
                throw e;
            }
        }

        return super.send();
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

        this.setRequestHeader("Content-Encoding", encoding);
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
     * @param type the value of the {@code Content-Type} header
     * @return this {@code HttpRequest} instance
     */
    public HttpRequestWithBody setContentType(final String type) {
        if (type == null)
            throw new NullPointerException("type == null");

        this.setRequestHeader("Content-Type", type);
        return this;
    }

    /**
     * Sets the {@code Message-Body} to send to the server.
     * 
     * @param body the {@code Message-Body} to send to the server
     * @return this {@code HttpRequest} instance
     * @throws IOException if an I/O error occurs
     */
    public HttpRequestWithBody setMessageBody(final RequestBody body) throws IOException {
        if (body == null)
            throw new NullPointerException("body = null");

        this.body = body;

        return this;
    }

}