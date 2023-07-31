package work.gaigeshen.formwork.basal.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author gaigeshen
 */
public class GuavaRatelimiterCreator implements RatelimiterCreator {

    private final Map<String, Ratelimiter> rateLimiters = new ConcurrentHashMap<>();

    private final double permitsPerSecond;

    public GuavaRatelimiterCreator(double permitsPerSecond) {
        if (permitsPerSecond <= 0) {
            throw new IllegalArgumentException("permitsPerSecond");
        }
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    public Ratelimiter create(String name, double permitsPerSecond) {
        return rateLimiters.computeIfAbsent(name, n -> {
            RateLimiter rateLimiter = RateLimiter.create(permitsPerSecond);
            return new GuavaRatelimiter(rateLimiter, n);
        });
    }

    /**
     *
     * @author gaigeshen
     */
    public static class GuavaRatelimiter implements Ratelimiter {

        private final RateLimiter rateLimiter;

        private final String name;

        public GuavaRatelimiter(RateLimiter rateLimiter, String name) {
            this.rateLimiter = rateLimiter;
            this.name = name;
        }

        @Override
        public boolean tryAcquire(long permits, Duration timeout) {
            return rateLimiter.tryAcquire((int) permits, timeout);
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
