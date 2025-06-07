package software.leonov.client.http;

import java.util.concurrent.TimeUnit;

/**
 * A rate limiter based on a dead-simple fixed interval algorithm.
 * <p>
 * This class is not designed to be used by the general public. It only supports one single blocking permit acquisition.
 */
final class SimpleRateLimiter implements RateLimiter {

    private final double rate;
    private final long   intervalNanos;
    private long         lastAcquireTime = 0;

    public SimpleRateLimiter(final double rate) {
        this.rate          = rate;
        this.intervalNanos = (long) (TimeUnit.SECONDS.toNanos(1) / rate);
    }

    public static SimpleRateLimiter create(final double rate) {
        if (rate <= 0.0)
            throw new IllegalArgumentException("rate <= 0.0");
        if (Double.isNaN(rate))
            throw new IllegalArgumentException("rate is Not-a-Number (NaN)");

        return new SimpleRateLimiter(rate);
    }

    @Override
    public synchronized void acquire() {
        long now = System.nanoTime();

        if (lastAcquireTime == 0) {
            lastAcquireTime = now;
            return;
        }

        long timeSinceLastAcquire = now - lastAcquireTime;
        long timeToWait           = intervalNanos - timeSinceLastAcquire;

        if (timeToWait > 0) {
            try {
                TimeUnit.NANOSECONDS.sleep(timeToWait);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lastAcquireTime = System.nanoTime();
        } else
            lastAcquireTime = now;
    }

    @Override
    public double getRate() {
        return rate;
    }

}