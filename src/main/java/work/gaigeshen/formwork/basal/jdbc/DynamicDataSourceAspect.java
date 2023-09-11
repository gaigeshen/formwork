package work.gaigeshen.formwork.basal.jdbc;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 动态数据源切面，注意此切面相对于其他数据源切面的顺序
 *
 * @author gaigeshen
 */
@Aspect
public class DynamicDataSourceAspect {

    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    private final DynamicDataSourceContextCreator dataSourceContextCreator;

    public DynamicDataSourceAspect(DynamicDataSourceContextCreator dataSourceContextCreator) {
        if (Objects.isNull(dataSourceContextCreator)) {
            throw new IllegalArgumentException("dataSourceContextCreator cannot be null");
        }
        this.dataSourceContextCreator = dataSourceContextCreator;
    }

    @Before("@annotation(dataSource)")
    public void changeDataSourceContext(DynamicDataSource dataSource) {
        String dynamicContextName = dataSource.value();
        if (Objects.nonNull(dynamicContextName)) {
            DynamicDataSourceContext dynamicDataSourceContext = dataSourceContextCreator.create(dynamicContextName);
            if (Objects.nonNull(dynamicDataSourceContext)) {
                DynamicDataSourceContextHolder.setContext(dynamicDataSourceContext);
                if (log.isDebugEnabled()) {
                    log.debug("dynamic data source context: {}", dynamicDataSourceContext);
                }
            }
        }
    }

    @After("@annotation(DynamicDataSource)")
    public void restoreDataSourceContext() {
        DynamicDataSourceContext dynamicDataSourceContext = DynamicDataSourceContextHolder.getContext();
        if (log.isDebugEnabled()) {
            log.debug("clear dynamic data source context: {}", dynamicDataSourceContext);
        }
        DynamicDataSourceContextHolder.clear();
    }
}
