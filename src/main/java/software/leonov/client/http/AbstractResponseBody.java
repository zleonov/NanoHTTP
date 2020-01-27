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
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * A skeletal implementation of the {@code ResponseBody} interface.
 * 
 * @author Zhenya Leonov
 */
abstract class AbstractResponseBody implements ResponseBody {

    private final Charset charset;

    /**
     * Constructs an {@code AbstractResponseBody}.
     * 
     * @param charset the {@code Content-Charset} of the response
     */
    public AbstractResponseBody(final Charset charset) {
        if (charset == null)
            throw new NullPointerException("charset == null");

        this.charset = charset;
    }

    @Override
    public abstract InputStream getInputStream() throws IOException;

    /**
     * Returns a byte array containing all the bytes read from this {@code Message-Body}.
     * 
     * @return a byte array containing all the bytes read from this {@code Message-Body}
     * @throws IOException if an I/O error occurs
     */
    public final byte[] toByteArray() throws IOException {
        return toByteArray(getInputStream());
    }

    /**
     * Returns this {@code Message-Body} as a string. The charset used is determined by
     * {@link HttpResponse#getContentCharset()}.
     * 
     * @return this {@code Message-Body} as a string
     * @throws IOException if an I/O error occurs
     */
    public final String asString() throws IOException {
        return new String(toByteArray(), charset);
    }

    static byte[] toByteArray(final InputStream in) throws IOException {

        int length = 8192;

        byte[] bytes = new byte[length];
        int total = 0;
        int n;

        do {
            while ((n = in.read(bytes, total, length - total)) > 0)
                total += n;

            if ((n = in.read()) != -1) {
                bytes = Arrays.copyOf(bytes, (length *= 2) > Integer.MAX_VALUE ? Integer.MAX_VALUE : length);
                bytes[total++] = (byte) n;
            }
        } while (n != -1);

        return bytes.length == total ? bytes : Arrays.copyOf(bytes, total);
    }

}
