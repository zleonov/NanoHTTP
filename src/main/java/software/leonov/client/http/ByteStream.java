package software.leonov.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

final class ByteStream {

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static final int BUFFER_SIZE = 8192;

    private ByteStream() {
    }

    static void copy(final InputStream from, final OutputStream to) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int          r;
        while ((r = from.read(buffer)) != -1)
            to.write(buffer, 0, r);
    }

    static byte[] toByteArray(final InputStream in) throws IOException {
        try {
            return toByteArray(in, Integer.MAX_VALUE);
        } catch (final SizeLimitExceededException e) {
            throw new AssertionError(); // cannot happen
        }
    }

    static byte[] toByteArray(final InputStream in, final int maxSize) throws IOException, SizeLimitExceededException {
        int length = BUFFER_SIZE;

        byte[] bytes = new byte[length];
        int    total = 0;
        int    n;

        do {
            while ((n = in.read(bytes, total, (int) length - total)) > 0)
                total += n;

            if (total > maxSize)
                throw new SizeLimitExceededException();

            if ((n = in.read()) != -1) {
                length         = (length *= 2) > MAX_ARRAY_SIZE ? MAX_ARRAY_SIZE : length;
                bytes          = Arrays.copyOf(bytes, (int) length);
                bytes[total++] = (byte) n;
            }
        } while (n != -1);

        return bytes.length == total ? bytes : Arrays.copyOf(bytes, total);
    }

}
