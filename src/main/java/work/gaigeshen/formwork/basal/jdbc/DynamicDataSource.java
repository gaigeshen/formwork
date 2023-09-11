package work.gaigeshen.formwork.basal.jdbc;

import java.lang.annotation.*;

/**
 * 动态数据源注解可以用在类上和方法上，用于动态切换目标数据源
 *
 * @author gaigeshen
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicDataSource {

    /**
     * 这里配置的是动态数据源上下文名称
     *
     * @return 动态数据源上下文名称
     */
    String value();
}
