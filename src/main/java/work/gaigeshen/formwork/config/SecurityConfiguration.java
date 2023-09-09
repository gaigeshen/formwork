package work.gaigeshen.formwork.config;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import work.gaigeshen.formwork.basal.security.AbstractAuthenticationProvider;
import work.gaigeshen.formwork.basal.security.AuthorizationExpiredEventListener;
import work.gaigeshen.formwork.basal.security.accesstoken.AccessTokenCreator;
import work.gaigeshen.formwork.basal.security.accesstoken.JWTAccessTokenCreator;
import work.gaigeshen.formwork.basal.security.crypto.CryptoProcessor;
import work.gaigeshen.formwork.basal.security.userdetails.superadmin.SuperAdminAuthenticationProvider;
import work.gaigeshen.formwork.basal.security.userdetails.superadmin.SuperAdminProperties;
import work.gaigeshen.formwork.basal.security.web.AbstractAccessDeniedHandler;
import work.gaigeshen.formwork.basal.security.web.DefaultAccessDeniedHandler;
import work.gaigeshen.formwork.basal.security.web.authentication.AbstractAuthenticationFilter;
import work.gaigeshen.formwork.basal.security.web.authentication.AbstractAutoAuthenticationFilter;
import work.gaigeshen.formwork.basal.security.web.authentication.DefaultAuthenticationFilter;
import work.gaigeshen.formwork.basal.security.web.authentication.JWTAccessTokenAutoAuthenticationFilter;
import work.gaigeshen.formwork.basal.security.web.logout.AbstractLogoutHandler;
import work.gaigeshen.formwork.basal.security.web.logout.DefaultAccessTokenLogoutHandler;

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
    public CryptoProcessor cryptoProcessor() {
        return CryptoProcessor.createDefault("0123456789abcdef");
    }

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
        return JWTAccessTokenCreator.create("0123456789abcdef");
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
        return new JWTAccessTokenAutoAuthenticationFilter(accessTokenCreator());
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
                .antMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                .antMatchers("/api-docs/**", "/ui-docs/**", "/swagger-ui/**", "/test/**").permitAll()
                .anyRequest().authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.csrf().disable().cors().configurationSource(r -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedMethods(Arrays.asList("GET", "POST"));
            config.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
            config.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
            config.setExposedHeaders(Collections.singletonList("X-Auth-Token"));
            config.setMaxAge(Duration.ofMinutes(30));
            return config;
        });
        return http.build();
    }

    @EnableConfigurationProperties(SuperAdminProperties.class)
    @Configuration
    static class AuthenticationManagerConfiguration {

        private final List<AbstractAuthenticationProvider> authenticationProviders;

        public AuthenticationManagerConfiguration(List<AbstractAuthenticationProvider> authenticationProviders) {
            this.authenticationProviders = authenticationProviders;
        }

        @Bean
        public AuthenticationManager authenticationManager(SuperAdminProperties superAdminProperties, PasswordEncoder passwordEncoder) {
            if (BooleanUtils.toBoolean(superAdminProperties.getEnabled())) {
                authenticationProviders.add(new SuperAdminAuthenticationProvider(superAdminProperties, passwordEncoder));
            }
            if (authenticationProviders.isEmpty()) {
                return new ProviderManager(Collections.singletonList(new AbstractAuthenticationProvider() { }));
            }
            return new ProviderManager(new ArrayList<>(authenticationProviders));
        }
    }
}
