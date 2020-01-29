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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A {@code RequestBody} backed by a byte array buffer.
 * 
 * @author Zhenya Leonov
 */
public class ByteArrayRequestBody implements RequestBody {

    private final byte[] bytes;
    private final int offset;
    private final int length;

    private String contentType = null;

    /**
     * Constructs a new {@code ByteArrayRequestBody} using the specified byte array as the backing buffer.
     * 
     * @param bytes the backing byte array buffer
     */
    public ByteArrayRequestBody(final byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    /**
     * Constructs a new {@code ByteArrayRequestBody} using the specified byte array with the specified {@code offset} and
     * {@code length} as the backing buffer.
     * 
     * @param bytes  the byte array buffer
     * @param offset the offset in the buffer of the first byte to read
     * @param length the maximum number of bytes to read from the buffer
     */
    public ByteArrayRequestBody(final byte[] bytes, final int offset, final int length) {
        if (bytes == null)
            throw new NullPointerException("bytes == null");
        if (offset < 0)
            throw new IllegalArgumentException("offset < 0");
        if (length < 0)
            throw new IllegalArgumentException("length < 0");
        if (offset + length > bytes.length)
            throw new IllegalArgumentException("offset + length > bytes.length");

        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }

    /**
     * Returns a new {@code ByteArrayRequestBody} whose buffer is {@link String#getBytes(Charset) encoded} from the
     * specified string using the UTF-8 charset.
     * 
     * @param body the string to encode
     * @return a new {@code ByteArrayRequestBody} whose buffer is {@link String#getBytes(Charset) encoded} from the
     *         specified string using the UTF-8 charset
     */
    public static ByteArrayRequestBody encode(final String body) {
        return new ByteArrayRequestBody(body.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns a new {@code ByteArrayRequestBody} whose buffer is {@link String#getBytes(Charset) encoded} from the
     * specified string using the given charset.
     * 
     * @param body    the string to encode
     * @param charset the specified charset
     * @return a new {@code ByteArrayRequestBody} whose buffer is {@link String#getBytes(Charset) encoded} from the
     *         specified string using the given charset
     */
    public static ByteArrayRequestBody encode(final String body, final Charset charset) {
        if (body == null)
            throw new NullPointerException("body == null");
        if (charset == null)
            throw new NullPointerException("charset == null");

        return new ByteArrayRequestBody(body.getBytes(charset));
    }

    /**
     * Returns the length of the backing byte array buffer.
     * 
     * @return the length of the backing byte array buffer
     */
    @Override
    public long getLength() {
        return length;
    }

    @Override
    public String getType() {
        return contentType;
    }

    /**
     * Writes the backing byte array buffer to the specified output stream.
     * <p>
     * This implementation is more efficient than the {@link RequestBody#write(OutputStream) default implementation}.
     * 
     * @param to the specified output stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(final OutputStream to) throws IOException {
        if (to == null)
            throw new NullPointerException("out == null");

        to.write(bytes, offset, length);
        to.flush();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes, offset, length);
    }

    ByteArrayRequestBody setType(final String type) {
        this.contentType = type;
        return this;
    }

}
