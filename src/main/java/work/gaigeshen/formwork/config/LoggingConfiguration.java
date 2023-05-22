package work.gaigeshen.formwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.gaigeshen.formwork.basal.logging.ThreadTraceIdContext;
import work.gaigeshen.formwork.basal.logging.TraceIdContext;
import work.gaigeshen.formwork.basal.logging.aop.LoggingAspect;

@Configuration
public class LoggingConfiguration {

    @Bean
    public LoggingAspect loggingAspect(TraceIdContext traceIdContext) {
        return new LoggingAspect(traceIdContext, records -> {

        });
    }

    @Bean
    public TraceIdContext traceIdContext() {
        return new ThreadTraceIdContext();
    }
}
