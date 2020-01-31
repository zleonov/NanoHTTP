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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GZipEncodingTest {

    private static String content;
    private static byte[] bytes;
    private static byte[] gz;
    private static RequestBody body;
    private static GZipEncoding encoded;
    private static GZipEncoding streaming;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        content = "The quick brown fox jumps over the lazy dog";
        bytes = content.getBytes(StandardCharsets.UTF_8);
        body = ByteArrayBody.encode(content, StandardCharsets.UTF_8);
        encoded = GZipEncoding.encode(body);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream out = new GZIPOutputStream(baos);
        out.write(bytes);
        out.close();

        gz = baos.toByteArray();
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        streaming = GZipEncoding.stream(body);
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void lengthEncoded() throws IOException {
        assertEquals(gz.length, encoded.length());
    }

    @Test
    void lengthStreaming() throws IOException {
        assertEquals(-1, streaming.length());
    }

    @Test
    void lengthStreamingAfterGetInputStream() throws IOException {
        streaming.inputStream();
        assertEquals(gz.length, streaming.length());
    }

    @Test
    void getInputStreamEncoded() throws IOException {
        try (final InputStream in = encoded.inputStream()) {
            final byte[] bytes = HttpResponse.toByteArray(in);
            assertArrayEquals(gz, bytes);
        }
    }

    @Test
    void getInputStreamStreaming() throws IOException {
        try (final InputStream in = streaming.inputStream()) {
            final byte[] bytes = HttpResponse.toByteArray(in);
            assertArrayEquals(gz, bytes);
        }
    }

    @Test
    void getEncodedWriteTo() throws IOException {
        final ByteArrayOutputStream to = new ByteArrayOutputStream();
        encoded.write(to);
        final byte[] bytes = to.toByteArray();
        assertArrayEquals(gz, bytes);
    }

    @Test
    void getStreamingWriteTo() throws IOException {
        final ByteArrayOutputStream to = new ByteArrayOutputStream();
        streaming.write(to);
        final byte[] bytes = to.toByteArray();
        assertArrayEquals(gz, bytes);
    }

}
