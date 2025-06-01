package software.leonov.client.http;

/**
 * An exception thrown when the size of a response exceeds the maximum specified threshold.
 * 
 * @author Zhenya Leonov
 */
public final class SizeLimitExceededException extends Exception {

    private static final long serialVersionUID = -4216380772254161673L;

    /**
     * Constructs an {@code SizeLimitExceededExceptions} with no message.
     */
    public SizeLimitExceededException() {
        super();
    }

    /**
     * Constructs an {@code SizeLimitExceededExceptions} with the specified detail message.
     *
     * @param message the detail message
     */
    public SizeLimitExceededException(final String message) {
        super(message);
    }

}
