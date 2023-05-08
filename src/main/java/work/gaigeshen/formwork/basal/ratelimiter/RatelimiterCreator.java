package work.gaigeshen.formwork.basal.ratelimiter;

/**
 * 用于创建限流器
 *
 * @author gaigeshen
 */
public interface RatelimiterCreator {

    /**
     * 创建限流器
     *
     * @param name 限流器名称，将作为不同的限流器标识符
     * @param permitsPerSecond 限流器每秒产生多少个许可
     * @return 返回的限流器不能为空
     */
    Ratelimiter create(String name, double permitsPerSecond);
}
