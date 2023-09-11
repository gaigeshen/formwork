package work.gaigeshen.formwork.basal.jdbc;

/**
 * 动态数据源上下文接口
 *
 * @author gaigeshen
 */
public interface DynamicDataSourceContext {

    /**
     * 返回动态数据源上下文名称
     *
     * @return 动态数据源上下文名称
     */
    String getName();
}
