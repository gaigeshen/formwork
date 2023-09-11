package work.gaigeshen.formwork.basal.jdbc;

/**
 * 用于创建动态数据源上下文
 *
 * @author gaigeshen
 */
public interface DynamicDataSourceContextCreator {

    /**
     * 创建动态数据源上下文
     *
     * @param dynamicContextName 动态数据源上下文名称
     * @return 动态数据源上下文
     */
    DynamicDataSourceContext create(String dynamicContextName);
}
