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
     * Returns an input stream which reads this {@code Message-Body}.
     * <p>
     * Whether or not <i>retry</i> is supported, that is whether or not this method will produce independent input streams
     * to the underlying source on subsequent calls, is implementation dependent. Subsequent calls may return the same input
     * stream (which will be empty if it has been exhausted), or may end up throwing a {@code RuntimeException}.
     * 
     * @return an input stream which reads this {@code Message-Body}
     * @throws IOException if an I/O error occurs
     */
    public InputStream stream() throws IOException;

    /**
     * Writes this {@code Message-Body} to the specified output stream. Closes the input stream. Does not close the output
     * stream.
     * <p>
     * The default implementation writes all bytes from the {@link #stream() input stream} to the output stream.
     * 
     * @param to the specified output stream
     * @throws IOException if an I/O error occurs
     */
    default void write(final OutputStream to) throws IOException {
        if (to == null)
            throw new NullPointerException("to == null");

        try (final InputStream from = stream()) {
            final byte[] buffer = new byte[8192];
            int r;
            while ((r = from.read(buffer)) != -1)
                to.write(buffer, 0, r);
        }

        to.flush();
    }

}