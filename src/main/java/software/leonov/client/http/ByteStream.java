package software.leonov.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

final class ByteStream {

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
        int length = BUFFER_SIZE;

        byte[] bytes = new byte[length];
        int    total = 0;
        int    n;

        do {
            while ((n = in.read(bytes, total, length - total)) > 0)
                total += n;

            if ((n = in.read()) != -1) {
                bytes          = Arrays.copyOf(bytes, (length *= 2) > Integer.MAX_VALUE ? Integer.MAX_VALUE : length);
                bytes[total++] = (byte) n;
            }
        } while (n != -1);

        return bytes.length == total ? bytes : Arrays.copyOf(bytes, total);
    }

}
