package work.gaigeshen.formwork.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springdoc.core.Constants;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 接口文档配置
 *
 * @author gaigeshen
 */
@SecuritySchemes({
        @SecurityScheme(
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.HEADER,
                name = "token",
                paramName = "X-Auth-Token")})
@OpenAPIDefinition(
        info = @Info(
                title = "Open API document",
                description = "This is a sample Open API document",
                version = "1.0.0-SNAPSHOT",
                contact = @Contact(
                        name = "gaigeshen",
                        email = "gaigeshen@qq.com",
                        url = "https://github.com/gaigeshen")),
        security = {
                @SecurityRequirement(name = "token")
        }
)
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public GroupedOpenApi defaultGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch(Constants.ALL_PATTERN)
                .build();
    }
}
