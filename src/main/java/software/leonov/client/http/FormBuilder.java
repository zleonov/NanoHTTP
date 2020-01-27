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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * A builder of
 * <a href="https://url.spec.whatwg.org/#concept-urlencoded" target="_blank">application/x-www-form-urlencoded</a>
 * content.
 * <p>
 * The parameter names and values are encoded in key-value tuples separated the ampersand (&) symbol, with an equals (=)
 * symbol between the name and the value. Non-alphanumeric characters in both keys and values are percent encoded: this
 * is the reason why this type is not suitable to use with binary data (use multipart/form-data instead).
 * <p>
 * When {@link #encode(String, String) encoding} parameter names and values, the following rules apply:
 * <ul>
 * <li>The characters {@code a}-{@code z}, {@code A}-{@code Z}, {@code 0}-{@code 9}, {@code .}, {@code -}, {@code *},
 * and {@code _} remain the same.
 * <li>The space character is converted into a plus sign "{@code +}".
 * <li>All other characters are unsafe and are first converted into one or more bytes using some encoding scheme. Then
 * each byte is represented by the 3-character string "<i>{@code %xy}</i>", where <i>xy</i> is the two-digit hexadecimal
 * representation of the byte.<br>
 * <b>The recommended encoding scheme to use is UTF-8.</b>
 * </ul>
 * 
 * @author Zhenya Leonov
 */
final public class FormBuilder {

    /**
     * The {@code Content-Type} constant: {@code application/x-www-form-urlencoded}
     */

    private final StringBuilder buffer = new StringBuilder();

    /**
     * Constructs a new {@code FormBuilder} which encode key-value pairs using the UTF-8 charset.
     */
    public FormBuilder() {
    }

    /**
     * Adds a new parameter. The name and value will <b>not</b> automatically be encoded to the
     * {@code application/x-www-form-urlencoded} format.
     * <p>
     * The intention is to use this method when the parameter name and value are already encoded.
     * 
     * @param name  the name
     * @param value the value
     * @return this {@code FormBuilder} instance
     */
    public FormBuilder add(final String name, final String value) {
        if (name == null)
            throw new NullPointerException("name == null");
        if (value == null)
            throw new NullPointerException("value == null");

        buffer.append('&').append(name).append('=').append(value);

        return this;
    }

    /**
     * Adds a new parameter. The name and value will automatically be encoded to the
     * {@code application/x-www-form-urlencoded} format, see {@link URLEncoder} for more information.
     * 
     * @param name  the name
     * @param value the value
     * @return this {@code FormBuilder} instance
     */
    public FormBuilder encode(final String name, final String value) {
        if (name == null)
            throw new NullPointerException("name == null");
        if (value == null)
            throw new NullPointerException("value == null");

        try {
            buffer.append('&').append(URLEncoder.encode(name, "UTF-8")).append('=').append(URLEncoder.encode(value, "UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            throw new AssertionError(e); // cannot happen
        }

        return this;
    }

    /**
     * Returns the {@code application/x-www-form-urlencoded} string of all parameters added to this builder.
     * 
     * @return the {@code application/x-www-form-urlencoded} string of all parameters added to this builder
     */
    @Override
    public String toString() {
        return buffer.substring(1);
    }

    /**
     * Returns a new {@code ByteArrayRequestBody} whose buffer is encoded from the parameters added to this builder using
     * the UTF-8 charset.
     * 
     * @return a new {@code ByteArrayRequestBody} whose buffer is encoded from the parameters added to this builder using
     *         the UTF-8 charset
     */
    public ByteArrayRequestBody build() {
        return ByteArrayRequestBody.encode(toString(), StandardCharsets.UTF_8).setContentType("application/x-www-form-urlencoded");
    }

}
