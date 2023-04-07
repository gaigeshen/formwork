package work.gaigeshen.formwork.commons.ratelimiter;

import java.lang.annotation.*;

/**
 * 限流注解
 *
 * @author gaigeshen
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    String value() default "default";

    double permitsPerSecond() default Double.MAX_VALUE;

    long timeout() default 0;

}
