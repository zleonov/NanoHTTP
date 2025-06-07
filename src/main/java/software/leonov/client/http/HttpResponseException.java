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
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Thrown when an HTTP response does not contain a <i>2xx</i> {@link HttpResponse#getStatusCode() Status-Code}.
 * 
 * @author Zhenya Leonov
 */
public class HttpResponseException extends IOException {

    private static final long serialVersionUID = 1016328592873099463L;

    private int statusCode = -1;
    private ResponseBody response = null;
    private URL from = null;
    private Map<String, List<String>> headers = null;

    /**
     * Constructs an {@code HttpResponseException} with the specified detail message.
     *
     * @param message the detail message or {@code null}
     */
    public HttpResponseException(final String message) {
        super(message, null);
    }

    /**
     * Constructs an {@code HttpResponseException} with the specified detail message and cause.
     *
     * @param message the detail message or {@code null}
     * @param cause   the cause, which can be later retrieved by {@link #getCause()} or {@code null}
     */
    public HttpResponseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Sets the response returned from the server.
     * 
     * @param response the response returned from the server or {@code null}
     * @return this {@code HttpResponseException} instance
     */
    HttpResponseException setServerResponse(final ResponseBody response) {
        this.response = response;
        return this;
    }

    /**
     * Sets the response headers sent by the server.
     * 
     * @param headers the response headers sent by the server or {@code null}
     * @return this {@code HttpResponseException} instance
     */
    HttpResponseException setResponseHeaders(final Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Sets the {@code Status-Code} return by the server.
     * 
     * @param statusCode the {@code Status-Code} return by the server or -1
     * @return this {@code HttpResponseException} instance
     */
    HttpResponseException setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Sets the {@code URL} responsible for this exception.
     * 
     * @param from the {@code URL} responsible for this exception or {@code null}
     * @return this {@code HttpResponseException} instance
     */
    HttpResponseException from(final URL from) {
        this.from = from;
        return this;
    }

    /**
     * Returns the response sent by the server or {@code null} if it is not available.
     * 
     * @return the response sent by the server or {@code null} if it is not available
     */
    public ResponseBody getServerReponse() {
        return response;
    }

    /**
     * Returns an unmodifiable {@code Map} of the response headers sent by the server or {@code null} if they are not
     * available.
     * <p>
     * The keys are strings that represent the response-header field names, the values are unmodifiable {@code List}s of
     * strings that represents the corresponding field values.
     * 
     * @return an unmodifiable {@code Map} of the response headers sent by the server or {@code null} if they are not
     *         available
     */
    public Map<String, List<String>> getResponseHeaders() {
        return headers;
    }

    /**
     * Returns the {@code Status-Code} parsed from the HTTP response or -1 if it is not available.
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
     * @return the {@code Status-Code} parsed from the HTTP response or -1 if it is not available
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the {@code URL} responsible for this exception or {@code null} if it is not available.
     * 
     * @return the {@code URL} responsible for this exception or {@code null} if it is not available
     */
    public URL from() {
        return from;
    }

}
