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
    public abstract InputStream stream() throws IOException;

    /**
     * Returns a byte array containing all the bytes read from this {@code Message-Body}.
     * 
     * @return a byte array containing all the bytes read from this {@code Message-Body}
     * @throws IOException if an I/O error occurs
     */
    public final byte[] toByteArray() throws IOException {
        return toByteArray(stream());
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
