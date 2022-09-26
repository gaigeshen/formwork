package work.gaigeshen.formwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import work.gaigeshen.formwork.security.AbstractAuthenticationProvider;
import work.gaigeshen.formwork.security.AuthenticationToken;
import work.gaigeshen.formwork.security.Authorization;
import work.gaigeshen.formwork.security.AuthorizationExpiredEventListener;
import work.gaigeshen.formwork.security.accesstoken.AccessTokenCreator;
import work.gaigeshen.formwork.security.accesstoken.DefaultAccessTokenCreator;
import work.gaigeshen.formwork.security.web.AbstractAccessDeniedHandler;
import work.gaigeshen.formwork.security.web.DefaultAccessDeniedHandler;
import work.gaigeshen.formwork.security.web.authentication.AbstractAuthenticationFilter;
import work.gaigeshen.formwork.security.web.authentication.AbstractAutoAuthenticationFilter;
import work.gaigeshen.formwork.security.web.authentication.AccessTokenAutoAuthenticationFilter;
import work.gaigeshen.formwork.security.web.authentication.DefaultAuthenticationFilter;
import work.gaigeshen.formwork.security.web.logout.AbstractLogoutHandler;
import work.gaigeshen.formwork.security.web.logout.DefaultAccessTokenLogoutHandler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 安全配置
 *
 * @author gaigeshen
 */
@EnableGlobalMethodSecurity(
        prePostEnabled = true, securedEnabled = true
)
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public AuthorizationExpiredEventListener authorizationExpiredEventListener() {
        return new AuthorizationExpiredEventListener(accessTokenCreator());
    }

    @Bean
    public AccessTokenCreator accessTokenCreator() {
        return DefaultAccessTokenCreator.create();
    }

    @Bean
    public AbstractAccessDeniedHandler accessDeniedHandler() {
        return new DefaultAccessDeniedHandler();
    }

    @Bean
    public AbstractLogoutHandler logoutHandler() {
        return new DefaultAccessTokenLogoutHandler(accessTokenCreator());
    }

    @Bean
    public AbstractAutoAuthenticationFilter autoAuthenticationFilter() {
        return new AccessTokenAutoAuthenticationFilter(accessTokenCreator());
    }

    @Bean
    public AbstractAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
        return new DefaultAuthenticationFilter(authenticationManager, accessTokenCreator());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(AbstractAuthenticationFilter authenticationFilter, HttpSecurity http) throws Exception {

        http.authenticationManager(authenticationFilter.getAuthenticationManager());

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(autoAuthenticationFilter(), authenticationFilter.getClass());

        AbstractAccessDeniedHandler accessDeniedHandler = accessDeniedHandler();
        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(accessDeniedHandler);

        AbstractLogoutHandler logoutHandler = logoutHandler();
        http.logout()
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(logoutHandler);

        http.authorizeRequests()
                .antMatchers("/api-docs/**", "/ui-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.csrf().disable().cors().configurationSource(r -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.setAllowedMethods(Arrays.asList("GET", "POST"));
            config.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
            config.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
            config.setExposedHeaders(Collections.singletonList("X-Auth-Token"));
            config.setMaxAge(Duration.ofMinutes(30));
            return config;
        });
        return http.build();
    }

    @Configuration
    static class AuthenticationManagerConfiguration {

        private final List<AbstractAuthenticationProvider> authenticationProviders;

        public AuthenticationManagerConfiguration(List<AbstractAuthenticationProvider> authenticationProviders) {
            this.authenticationProviders = authenticationProviders;
        }

        @Bean
        public AuthenticationManager authenticationManager() {
            if (authenticationProviders.isEmpty()) {
                return new ProviderManager(Collections.singletonList(new AbstractAuthenticationProvider() {
                    @Override
                    protected Authorization authenticate(AuthenticationToken token) throws AuthenticationException {
                        return null;
                    }
                }));
            }
            return new ProviderManager(new ArrayList<>(authenticationProviders));
        }
    }
}
