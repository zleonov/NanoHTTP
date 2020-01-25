package software.leonov.client.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class allows a {@code Message-Body} to be sent to the server using GZip compression.
 * <p>
 * For example:
 * 
 * <pre>
 * final URL dest = ...
 * final RequestBody body = ...
 * final String contentType = ...
 * 
 * final HttpClient http = HttpClient.getDefaultClient();
 * 
 * http.post(dest).setContentType(contentType).setContentEncoding("gzip").setMessageBody(GZipEncoding.stream(body)).send();
 * </pre>
 * 
 * or
 * 
 * <pre>
 * 
 * final GZipEncoding gz = GZipEncoding.encode(body);
 * http.post(dest).setContentType(contentType).setContentEncoding("gzip").setContentLength(gz.length()).setMessageBody(gz).send();
 * </pre>
 * 
 * @author Zhenya Leonov
 */
public final class GZipEncoding implements RequestBody {

    final private RequestBody body;
    private int length;
    private byte[] buffer;

    private GZipEncoding(final byte[] buffer) {
        body = null;
        this.buffer = buffer;
        length = buffer.length;
    }

    private GZipEncoding(final RequestBody body) {
        buffer = null;
        this.body = body;
        length = -1;
    }

    /**
     * Returns a new {@code GZipEncoding} which will stream the specified {@code Message-Body} to the server using GZip
     * compression.
     * <p>
     * The {@code Message-Body} is not compressed into a buffer in advance. The {@link #length()} method will return -1.
     * 
     * @param body the specified {@code Message-Body}
     */
    public static GZipEncoding stream(final RequestBody body) throws IOException {
        if (body == null)
            throw new NullPointerException("body == null");

        return new GZipEncoding(body);
    }

    /**
     * Returns a new {@code GZipEncoding} which compresses the specified {@code Message-Body} into a byte array buffer.
     * <p>
     * Use this method when the {@code Content-Length} of the HTTP request needs to be known in advance.
     * 
     * @param body the specified {@code Message-Body}
     * @return a new {@code GZipEncoding} which compresses the specified {@code Message-Body} into a byte array buffer
     * @throws IOException if an I/O error occurred
     */
    public static GZipEncoding encode(final RequestBody body) throws IOException {
        if (body == null)
            throw new NullPointerException("body == null");

        return new GZipEncoding(buffer(body));
    }

    /**
     * Returns the length of the backing byte array buffer or -1 if it has not been {@link #encode(RequestBody) buffered}.
     * 
     * @return the length of the backing byte array buffer or -1 if it has not been {@link #encode(RequestBody) buffered}
     */
    public int length() {
        return length;
    }

    /**
     * Returns an input stream which reads the underlying compressed {@code Message-Body}. Calling this method should be
     * avoided by the user since it will require compressing the underlying {@code Message-Body} into a byte array buffer
     * (if it has not been {@link #encode(RequestBody) buffered} already) even if the intent was to
     * {@link #stream(RequestBody) stream} the underlying {@code Message-Body}. This method is not called internally.
     * <p>
     * The contents of the input stream will have to be uncompressed.
     * 
     * @return an input stream which reads this {@code Message-Body}
     * @throws IOException if an I/O error occurs
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (buffer == null) {
            buffer = buffer(body);
            length = buffer.length;
        }

        return new ByteArrayInputStream(buffer);
    }

    /**
     * Writes the underlying {@code Message-Body} to the specified output stream using GZip compression. Closes the input
     * stream. Does not close the output stream.
     * 
     * @param to the specified output stream
     */
    @Override
    public void write(final OutputStream to) throws IOException {
        if (to == null)
            throw new NullPointerException("to == null");

        if (buffer != null) {
            to.write(buffer);
            to.flush();
        } else {
            final GZIPOutputStream out = new GZIPOutputStream(to, true);

            try (final InputStream from = body.getInputStream()) {
                final byte[] buffer = new byte[8192];
                int r;
                while ((r = from.read(buffer)) != -1)
                    out.write(buffer, 0, r);

                out.finish();
                out.flush();
            }
        }
    }

    private static byte[] buffer(final RequestBody body) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (final GZIPOutputStream to = new GZIPOutputStream(out); final InputStream from = body.getInputStream()) {
            final byte[] buffer = new byte[8192];
            int r;
            while ((r = from.read(buffer)) != -1)
                to.write(buffer, 0, r);
        }

        return out.toByteArray();
    }

}
