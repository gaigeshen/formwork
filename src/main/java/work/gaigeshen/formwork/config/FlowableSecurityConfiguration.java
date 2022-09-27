package work.gaigeshen.formwork.config;

import org.flowable.common.engine.api.identity.AuthenticationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.gaigeshen.formwork.security.SecurityContextUtils;

import java.security.Principal;

/**
 *
 * @author gaigeshen
 */
@Configuration
public class FlowableSecurityConfiguration {

    @Bean
    public AuthenticationContext authenticationContext() {
        return new AuthenticationContext() {
            @Override
            public String getAuthenticatedUserId() {
                return SecurityContextUtils.getAuthorizationUserId();
            }

            @Override
            public Principal getPrincipal() {
                return SecurityContextUtils.getAuthentication();
            }

            @Override
            public void setPrincipal(Principal principal) {
            }
        };
    }
}
