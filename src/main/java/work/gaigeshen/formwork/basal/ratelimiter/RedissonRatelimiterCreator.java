package work.gaigeshen.formwork.basal.ratelimiter;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 利用外部存储来创建限流器，限流器的状态将会被外部存储管理，所以可以用在集群环境中
 *
 * @author gaigeshen
 */
public class RedissonRatelimiterCreator implements RatelimiterCreator {

    private final Map<String, Ratelimiter> rateLimiters = new ConcurrentHashMap<>();

    private final RedissonClient redisson;

    public RedissonRatelimiterCreator(RedissonClient redisson) {
        if (Objects.isNull(redisson)) {
            throw new IllegalArgumentException("redisson client cannot be null");
        }
        this.redisson = redisson;
    }

    @Override
    public Ratelimiter create(String name, double permitsPerSecond) {
        return rateLimiters.computeIfAbsent(name, n -> {
            RRateLimiter rateLimiter = redisson.getRateLimiter(n);
            rateLimiter.trySetRate(RateType.PER_CLIENT, (long) permitsPerSecond, 1, RateIntervalUnit.SECONDS);
            return new RedissonRatelimiter(rateLimiter);
        });
    }

    /**
     *
     * @author gaigeshen
     */
    public static class RedissonRatelimiter implements Ratelimiter {

        private final RRateLimiter rateLimiter;

        public RedissonRatelimiter(RRateLimiter rateLimiter) {
            this.rateLimiter = rateLimiter;
        }

        @Override
        public boolean tryAcquire(long permits, Duration timeout) {
            return rateLimiter.tryAcquire(permits, timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public String getName() {
            return rateLimiter.getName();
        }
    }
}
