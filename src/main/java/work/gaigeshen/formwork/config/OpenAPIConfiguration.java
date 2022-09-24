package work.gaigeshen.formwork.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author gaigeshen
 */
@OpenAPIDefinition(info = @Info(
        title = "API document of formwork",
        description = "DEMO API document of formwork",
        version = "1.0.0-SNAPSHOT",
        contact = @Contact(
                name = "gaigeshen",
                url = "https://github.com/gaigeshen",
                email = "gaigeshen@qq.com"
        )
))
@Configuration
public class OpenAPIConfiguration {

}
