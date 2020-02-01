package software.leonov.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@code RequestBody} that generates independent input streams from a file.
 * 
 * @author Zhenya Leonov
 */
final public class FileBody implements RequestBody {

    private final Path file;
    private final long size;

    private String contentType = null;
    private String encoding = null;

    /**
     * Constructs a new {@code FileBody} that generates independent input streams from the specified file.
     * <p>
     * The {@code Content-Type} of the file is determined by {@link Files#probeContentType(Path)}.
     * 
     * @param file the specified file (symlinks are followed)
     * @throws IOException if an I/O error occurs
     */
    public FileBody(final Path file) throws IOException {
        if (file == null)
            throw new NullPointerException("path == null");

        if (!Files.isRegularFile(file))
            throw new IllegalArgumentException(file + " is not a regular file or does not exist");

        final String contentType = Files.probeContentType(file);
        if (contentType != null)
            setContentType(contentType);

        this.size = Files.size(file);
        this.file = file;
    }

    /**
     * Returns the {@code Content-Encoding} of the file or {@code null} if it is not specified.
     * 
     * @return the {@code Content-Encoding} of the file or {@code null} if it is not specified
     */
    @Override
    public String getContentEncoding() {
        return encoding;
    }

    /**
     * Returns the size of the file in bytes.
     * 
     * @return the size of the file in bytes
     */
    @Override
    public long length() {
        return size;
    }

    /**
     * Returns the {@code Content-Type} of the file or {@code null} if it is not specified.
     * 
     * @return the {@code Content-Type} of the file or {@code null} if it is not specified
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the name of the file. The file name is the <i>farthest</i> element from the root in the directory hierarchy.
     * 
     * @return the name of the file
     */
    public String filename() {
        return file.getFileName().toString();
    }

    /**
     * Returns a new {@code FileInputStream} to the file each time this method is called.
     * 
     * @return a new {@code FileInputStream} to the file
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(file);
    }

    /**
     * Sets the {@code Content-Encoding} of the file.
     * 
     * @param encoding the {@code Content-Encoding}
     * @return this {@code RequestBody} instance
     */
    public FileBody setContentEncoding(final String encoding) {
        if (encoding == null)
            throw new NullPointerException("encoding == null");

        this.encoding = encoding;
        return this;
    }

    /**
     * Sets the {@code Content-Type} of the file.
     * <p>
     * Use this method when you want to override the {@code Content-Type} determined by {@link Files#probeContentType(Path)}
     * when this {@code FileBody} was created.
     * 
     * @param contentType the {@code Content-Type}
     * @return this {@code RequestBody} instance
     */
    public FileBody setContentType(final String contentType) {
        if (contentType == null)
            throw new NullPointerException("contentType == null");

        this.contentType = contentType;
        return this;
    }

}
