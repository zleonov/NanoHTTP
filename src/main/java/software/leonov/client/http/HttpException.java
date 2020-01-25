package software.leonov.client.http;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * An exception thrown when attempting to read the {@code Message-Body} of an HTTP response which contains an error
 * {@link HttpResponse#isSuccessStatusCode() Status-Code}.
 * 
 * @author Zhenya Leonov
 */
public class HttpException extends IOException {

    private static final long serialVersionUID = 1016328592873099463L;

    private int statusCode = -1;
    private String errorMessage = null;
    private URL url = null;
    private Map<String, List<String>> responseHeaders = null;

    /**
     * Constructs an {@code HttpException} with the specified detail message.
     *
     * @param message the detail message or {@code null}
     */
    public HttpException(final String message) {
        super(message, null);
    }

    /**
     * Constructs an {@code HttpException} with the specified detail message and cause.
     *
     * @param message the detail message or {@code null}
     * @param cause   the cause, which can be later retrieved by {@link #getCause()}, or {@code null}
     */
    public HttpException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Sets the error message returned from the server.
     * 
     * @param errorMessage the error message returned from the server or {@code null}
     * @return this {@code HttpException} instance
     */
    HttpException setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    /**
     * Sets the response headers sent by the server.
     * 
     * @param responseHeaders the response headers sent by the server or {@code null}
     * @return this {@code HttpException} instance
     */
    HttpException setResponseHeaders(final Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
        return this;
    }

    /**
     * Sets the {@code Status-Code} return by the server.
     * 
     * @param statusCode the {@code Status-Code} return by the server or -1
     * @return this {@code HttpException} instance
     */
    HttpException setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Sets the {@code URL} responsible for this exception.
     * 
     * @param url the {@code URL} responsible for this exception or {@code null}
     * @return this {@code HttpException} instance
     */
    HttpException setURL(final URL url) {
        this.url = url;
        return this;
    }

    /**
     * Returns the error message sent by the server or {@code null} if it is not available or not known.
     * 
     * @return the error message sent by the server or {@code null}
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns an unmodifiable {@code Map} of the response headers sent by the server or {@code null} if they are not
     * available or not known.
     * <p>
     * The keys are strings that represent the response-header field names, the values are unmodifiable {@code List}s of
     * strings that represents the corresponding field values.
     * 
     * @return an unmodifiable {@code Map} of the response headers sent by the server or {@code null} if they are not
     *         available or not known
     */
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Returns the {@code Status-Code} parsed from the HTTP response or -1 if it is not available or not known.
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
     * @return the {@code Status-Code} parsed from the HTTP response or -1 if it is not available or not known
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the {@code URL} responsible for this exception or {@code null} if it is not available or not known.
     * 
     * @return the {@code URL} responsible for this exception or {@code null} if it is not available or not known
     */
    public URL getURL() {
        return url;
    }

}