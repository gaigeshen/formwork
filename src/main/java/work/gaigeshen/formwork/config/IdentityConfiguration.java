package work.gaigeshen.formwork.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.gaigeshen.formwork.basal.identity.IdentityGenerator;
import work.gaigeshen.formwork.basal.identity.UUIDIdentityGenerator;
import work.gaigeshen.formwork.basal.identity.serialnumber.RedissonSerialNumberGenerator;
import work.gaigeshen.formwork.basal.identity.serialnumber.SerialNumberGenerator;

/**
 * 序列号生成器和标识符生成器配置
 *
 * @author gaigeshen
 */
@Configuration
public class IdentityConfiguration {

    @Bean
    public SerialNumberGenerator serialNumberGenerator(RedissonClient redissonClient) {
        return new RedissonSerialNumberGenerator(redissonClient);
    }

    @Bean
    public IdentityGenerator identityGenerator() {
        return UUIDIdentityGenerator.INSTANCE;
    }
}
