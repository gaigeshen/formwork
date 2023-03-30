package work.gaigeshen.formwork.commons.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是默认的实现，不要在集群环境中使用
 *
 * @author gaigeshen
 * @see #create(double)
 */
public class GuavaRateLimiterService implements RateLimiterService {

    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    private final double permitsPerSecond;

    public GuavaRateLimiterService(double permitsPerSecond) {
        if (permitsPerSecond <= 0) {
            throw new IllegalArgumentException("permitsPerSecond");
        }
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    public void acquire(String key, int permits) {
        rateLimiters.computeIfAbsent(key, k -> {
            double permitsPerSecond = getPermitsPerSecond();
            return RateLimiter.create(permitsPerSecond);
        }).acquire(permits);
    }

    @Override
    public boolean tryAcquire(String key, int permits, Duration timeout) {
        return rateLimiters.computeIfAbsent(key, k -> {
            double permitsPerSecond = getPermitsPerSecond();
            return RateLimiter.create(permitsPerSecond);
        }).tryAcquire(permits, timeout);
    }

    @Override
    public double getPermitsPerSecond() {
        return permitsPerSecond;
    }
}
