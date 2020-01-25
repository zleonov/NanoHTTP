package software.leonov.client.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * The {@code Message-Body} of an HTTP response.
 * <p>
 * <b>Note:</b> Whether or not a {@code Message-Body} is buffered in the response is implementation dependent. Unless
 * otherwise stated, it should be assumed that this {@code ResponseBody} object represents a <i>one-shot</i> stream
 * backed by an active connection to the server, and may be consumed only <b>once</b> by calling the the
 * {@link ResponseBody#asString()}, {@link ResponseBody#toByteArray()}, or {@link ResponseBody#getInputStream()}
 * methods.
 * <p>
 * The response body <i>should</i> be consumed in it's entirety to allow the underlying {@code Connection} to be reused,
 * assuming <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/net/http-keepalive.html" target=
 * "_blank">keep-alive</a> is on. All underlying streams will be closed when this response is
 * {@link HttpResponse#close() closed}.
 * 
 * @author Zhenya Leonov
 */
public interface ResponseBody {

    /**
     * Returns an input stream which reads this {@code Message-Body}.
     * <p>
     * A {@code SocketTimeoutException} can be thrown when reading from the returned input stream if the read timeout
     * expires before data is available for read.
     * 
     * @return an input stream which reads this {@code Message-Body}
     * @throws IOException if an I/O error occurs
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Parses and returns this {@code Message-Body} as a string. The charset used is determined by
     * {@link HttpResponse#getContentCharset()}.
     * 
     * @return this {@code Message-Body} as a string
     * @throws IOException if an I/O error occurs
     */
    public String asString() throws IOException;

    /**
     * Returns a byte array containing all the bytes read from this {@code Message-Body}.
     * 
     * @return a byte array containing all the bytes read from this {@code Message-Body}
     * @throws IOException if an I/O error occurs
     */
    public byte[] toByteArray() throws IOException;

}
