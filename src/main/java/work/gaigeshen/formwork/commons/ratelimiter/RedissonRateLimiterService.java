package work.gaigeshen.formwork.commons.ratelimiter;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.redisson.api.RateIntervalUnit.SECONDS;
import static org.redisson.api.RateType.PER_CLIENT;

/**
 * 此限流服务可以用于集群环境
 *
 * @author gaigeshen
 */
public class RedissonRateLimiterService implements RateLimiterService {

    private final Map<String, RRateLimiter> rateLimiters = new ConcurrentHashMap<>();

    private final RedissonClient redisson;

    private final long permitsPerSecond;

    public RedissonRateLimiterService(RedissonClient redisson, long permitsPerSecond) {
        if (Objects.isNull(redisson)) {
            throw new IllegalArgumentException("redisson");
        }
        if (permitsPerSecond <= 0) {
            throw new IllegalArgumentException("permitsPerSecond");
        }
        this.redisson = redisson;
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    public void acquire(String key, int permits) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (permits <= 0) {
            throw new IllegalArgumentException("permits is invalid");
        }
        rateLimiters.computeIfAbsent(key, k -> {
            RRateLimiter rateLimiter = redisson.getRateLimiter(key);
            if (!rateLimiter.trySetRate(PER_CLIENT, (long) getPermitsPerSecond(), 1, SECONDS)) {
                throw new IllegalStateException("could not set rate of key: " + k);
            }
            return rateLimiter;
        }).acquire(permits);
    }

    @Override
    public double getPermitsPerSecond() {
        return permitsPerSecond;
    }
}