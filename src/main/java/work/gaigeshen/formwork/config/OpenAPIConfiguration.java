package work.gaigeshen.formwork.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 接口文档配置
 *
 * @author gaigeshen
 */
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI openAPI() {
        Contact contact = new Contact()
                .name("gaigeshen")
                .email("gaigeshen@qq.com")
                .url("https://github.com/gaigeshen");

        Info info = new Info()
                .title("Open API document")
                .description("This is a sample Open API document")
                .version("1.0.0-SNAPSHOT")
                .contact(contact);

        return new OpenAPI().info(info);
    }
}
