package work.gaigeshen.formwork.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import work.gaigeshen.formwork.basal.security.crypto.CryptoProcessor;
import work.gaigeshen.formwork.basal.security.web.crypto.CryptoHttpRequestInterceptor;

import java.time.Duration;

/**
 *
 *
 * @author gaigeshen
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.setConnectTimeout(Duration.ofSeconds(2)).setReadTimeout(Duration.ofSeconds(5)).build();
    }

    @Bean
    public RestTemplateCustomizer cryptoRestTemplateCustomizer(CryptoProcessor cryptoProcessor) {
        return new CryptoRestTemplateCustomizer(cryptoProcessor);
    }

    private static class CryptoRestTemplateCustomizer implements RestTemplateCustomizer {

        private final CryptoProcessor cryptoProcessor;

        public CryptoRestTemplateCustomizer(CryptoProcessor cryptoProcessor) {
            this.cryptoProcessor = cryptoProcessor;
        }

        @Override
        public void customize(RestTemplate restTemplate) {
            restTemplate.getInterceptors().add(new CryptoHttpRequestInterceptor(cryptoProcessor));
        }
    }
}
