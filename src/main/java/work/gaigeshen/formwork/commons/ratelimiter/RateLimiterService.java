package work.gaigeshen.formwork.commons.ratelimiter;

import java.time.Duration;

/**
 * 限流服务
 *
 * @author gaigeshen
 */
public interface RateLimiterService {

    /**
     * 创建限流服务对象实例，返回默认的实现
     *
     * @param permitsPerSecond 表示每秒许可数量
     * @return 限流服务对象实例
     * @see GuavaRateLimiterService
     */
    static RateLimiterService create(double permitsPerSecond) {
        return new GuavaRateLimiterService(permitsPerSecond);
    }

    /**
     * 获取许可前会阻塞
     *
     * @param key 限流针对的标识符，不同标识符限流隔离
     * @param permits 需要获取多少个许可
     */
    void acquire(String key, int permits);

    /**
     * 获取许可前会阻塞，获取单个许可
     *
     * @param key 限流针对的标识符，不同标识符限流隔离
     */
    default void acquire(String key) {
        acquire(key, 1);
    }

    /**
     * 在超时之前获取许可会阻塞
     *
     * @param key 限流针对的标识符，不同标识符限流隔离
     * @param permits 需要获取多少个许可
     * @param timeout 超时时间
     * @return 返回是否获取许可成功
     */
    boolean tryAcquire(String key, int permits, Duration timeout);

    /**
     * 在超时之前获取许可会阻塞，获取单个许可
     *
     * @param key 限流针对的标识符，不同标识符限流隔离
     * @param timeout 超时时间
     * @return 返回是否获取许可成功
     */
    default boolean tryAcquire(String key, Duration timeout) {
        return tryAcquire(key, 1, timeout);
    }

    /**
     * 返回设定的每秒许可数量
     *
     * @return 设定的每秒许可数量
     */
    double getPermitsPerSecond();
}
