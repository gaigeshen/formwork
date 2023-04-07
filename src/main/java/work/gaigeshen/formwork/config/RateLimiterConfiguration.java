package work.gaigeshen.formwork.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.gaigeshen.formwork.commons.ratelimiter.RatelimiterCreator;
import work.gaigeshen.formwork.commons.ratelimiter.RedissonRatelimiterCreator;
import work.gaigeshen.formwork.commons.ratelimiter.aop.RateLimiterAspect;

/**
 *
 * @author gaigeshen
 */
@Configuration
public class RateLimiterConfiguration {

    @Bean
    public RateLimiterAspect rateLimiterAspect(RatelimiterCreator ratelimiterCreator) {
        return new RateLimiterAspect(ratelimiterCreator);
    }

    @Bean
    public RatelimiterCreator ratelimiterCreator(RedissonClient redissonClient) {
        return new RedissonRatelimiterCreator(redissonClient);
    }
}
