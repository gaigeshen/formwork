package work.gaigeshen.formwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;

/**
 *
 * @author gaigeshen
 * @see org.springframework.retry.annotation.Retryable
 * @see org.springframework.retry.annotation.Recover
 */
@EnableRetry
@Configuration
public class RetryConfiguration {

    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder().fixedBackoff(1500).maxAttempts(3).build();
    }
}
