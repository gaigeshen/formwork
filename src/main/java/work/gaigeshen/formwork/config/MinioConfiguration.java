package work.gaigeshen.formwork.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置
 *
 * @author gaigeshen
 */
@EnableConfigurationProperties(MinioConfiguration.MinioProperties.class)
@Configuration
public class MinioConfiguration {

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    /**
     * 文件服务器配置
     *
     * @author gaigeshen
     */
    @ConfigurationProperties("spring.minio")
    public static class MinioProperties {

        /**
         * 文件服务器的访问地址
         */
        private String serverHost;

        /**
         * 文件服务器接口的访问地址
         */
        private String endpoint;

        /**
         * 文件服务器接口的访问凭证
         */
        private String accessKey;

        /**
         * 文件服务器接口的访问密钥
         */
        private String secretKey;

        public String getServerHost() {
            return serverHost;
        }

        public void setServerHost(String serverHost) {
            this.serverHost = serverHost;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }
}
