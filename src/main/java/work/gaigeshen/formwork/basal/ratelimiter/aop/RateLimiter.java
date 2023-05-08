package work.gaigeshen.formwork.basal.ratelimiter.aop;

import java.lang.annotation.*;

/**
 * 限流注解
 *
 * @author gaigeshen
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流器名称，用于产生不同的限流器，如果不显式设定此值，则会共享相同的限流器
     */
    String value() default "default";

    /**
     * 每秒产生多少个许可
     */
    double permitsPerSecond() default 1;

    /**
     * 获取许可之前等待的时间，单位毫秒
     */
    long timeout() default 1000;
}
