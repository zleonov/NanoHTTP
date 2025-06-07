package software.leonov.client.http;

interface RateLimiter {

    public default double getRate() {
        return Double.POSITIVE_INFINITY;
    }

    public default void acquire() {
    }

    public static RateLimiter unlimited() {
        return new RateLimiter() {
        };
    }

}
