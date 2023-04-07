package work.gaigeshen.formwork.commons.ratelimiter;

import java.time.Duration;

/**
 * 限流器
 *
 * @author gaigeshen
 */
public interface Ratelimiter {

    /**
     * 在超时之前获取许可会阻塞
     *
     * @param permits 需要获取多少个许可
     * @param timeout 超时时间
     * @return 返回是否获取许可成功
     */
    boolean tryAcquire(long permits, Duration timeout);

    /**
     * 在超时之前获取许可会阻塞，获取单个许可
     *
     * @param timeout 超时时间
     * @return 返回是否获取许可成功
     */
    default boolean tryAcquire(Duration timeout) {
        return tryAcquire(1, timeout);
    }

    /**
     * 获取限流器的名称
     *
     * @return 限流器的名称
     */
    String getName();
}
