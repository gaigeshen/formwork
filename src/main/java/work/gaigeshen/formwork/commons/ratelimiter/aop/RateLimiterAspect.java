package work.gaigeshen.formwork.commons.ratelimiter.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import work.gaigeshen.formwork.commons.ratelimiter.Ratelimiter;
import work.gaigeshen.formwork.commons.ratelimiter.RatelimiterCreator;
import work.gaigeshen.formwork.commons.ratelimiter.RatelimiterException;

import java.time.Duration;
import java.util.Objects;

/**
 * 限流处理切面
 *
 * @author gaigeshen
 */
@Aspect
public class RateLimiterAspect {

    private final RatelimiterCreator ratelimiterCreator;

    public RateLimiterAspect(RatelimiterCreator ratelimiterCreator) {
        if (Objects.isNull(ratelimiterCreator)) {
            throw new IllegalArgumentException("ratelimiterCreator");
        }
        this.ratelimiterCreator = ratelimiterCreator;
    }

    @Around("@annotation(rateLimiter)")
    public Object doAround(ProceedingJoinPoint jp, RateLimiter rateLimiter) throws Throwable {

        Ratelimiter ratelimiter = ratelimiterCreator.create(rateLimiter.value(), rateLimiter.permitsPerSecond());

        if (ratelimiter.tryAcquire(Duration.ofMillis(rateLimiter.timeout()))) {
            return jp.proceed();
        }

        throw new RatelimiterException(jp.getSignature().toString());
    }
}
