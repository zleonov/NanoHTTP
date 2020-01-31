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
import java.io.OutputStream;

/**
 * The {@code Message-Body} of an HTTP request.
 * 
 * @author Zhenya Leonov
 */
@FunctionalInterface
public interface RequestBody {

    /**
     * Returns an input stream which reads this {@code Message-Body} (optional operation). If this operation is not
     * supported, implementing classes must override the {@link #write(OutputStream)} method.
     * <p>
     * Whether or not this method will produce independent input streams to the underlying source on subsequent calls, is
     * implementation dependent. Subsequent calls may return the same input stream (which will be empty if it has been
     * exhausted), or may end up throwing an {@code Exception}.
     * 
     * @return an input stream which reads this {@code Message-Body}
     * @throws IOException                   if an I/O error occurs
     * @throws UnsupportedOperationException if this operation is not supported
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Returns the {@code Content-Encoding} of this {@code Message-Body} or {@code null} if it is not specified.
     * 
     * @return the {@code Content-Encoding} of this {@code Message-Body} or {@code null} if it is not specified
     */
    default String getContentEncoding() {
        return null;
    }

    /**
     * Returns the {@code Content-Type} of this {@code Message-Body} or {@code null} if it is not specified.
     * 
     * @return the {@code Content-Type} of this {@code Message-Body} or {@code null} if it is not specified
     */
    default String getContentType() {
        return null;
    }

    /**
     * Returns the length of this {@code Message-Body} in bytes or -1 if it is not specified.
     * 
     * @return the length of this {@code Message-Body} in bytes or -1 if it is not specified
     */
    default long length() {
        return -1;
    }

    /**
     * Writes this {@code Message-Body} to the specified output stream. Closes the {@link #getInputStream() input stream} if
     * any exists. Does not close the output stream.
     * <p>
     * The default implementation writes all bytes from the {@link #getInputStream() input stream} to the output stream.
     * 
     * @param to the specified output stream
     * @throws IOException if an I/O error occurs
     */
    default void write(final OutputStream to) throws IOException {
        if (to == null)
            throw new NullPointerException("to == null");

        try (final InputStream from = getInputStream()) {
            final byte[] buffer = new byte[8192];
            int r;
            while ((r = from.read(buffer)) != -1)
                to.write(buffer, 0, r);
        }

        to.flush();
    }

}