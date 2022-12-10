package work.gaigeshen.formwork.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import work.gaigeshen.formwork.commons.web.trace.TraceIdFilter;

import java.util.Collections;

/**
 *
 * @author gaigeshen
 */
@Configuration
public class TraceIdFilterConfiguration {

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        TraceIdFilter filter = new TraceIdFilter();
        FilterRegistrationBean<TraceIdFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setUrlPatterns(Collections.singletonList("/*"));
        filterBean.setFilter(filter);
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterBean;
    }
}
