package work.gaigeshen.formwork.basal.jdbc;

/**
 * 动态数据源上下文持有，用于设定和获取后续所用的数据源信息
 *
 * @author gaigeshen
 */
public abstract class DynamicDataSourceContextHolder {

    private static final ThreadLocal<DynamicDataSourceContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    private DynamicDataSourceContextHolder() { }

    public static void setContext(DynamicDataSourceContext context) {
        CONTEXT_THREAD_LOCAL.set(context);
    }

    public static DynamicDataSourceContext getContext() {
        return CONTEXT_THREAD_LOCAL.get();
    }

    public static void clear() {
        CONTEXT_THREAD_LOCAL.remove();
    }
}
