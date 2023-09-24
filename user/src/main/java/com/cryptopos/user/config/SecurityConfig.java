package com.cryptopos.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

import com.cryptopos.user.security.AppReactiveUserDetailsService;

import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {

    @Bean
    public AppReactiveUserDetailsService userDetailsService() {
        return new AppReactiveUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/signup")
                        .permitAll()

                        .pathMatchers(HttpMethod.POST, "/employee")
                        .hasAnyRole("GLOBAL_ADMINISTRATOR", "BRANCH_MANAGER")

                        .pathMatchers(HttpMethod.PUT, "/employee/{employeeId}")
                        .hasAnyRole("GLOBAL_ADMINISTRATOR", "BRANCH_MANAGER")

                        .pathMatchers(HttpMethod.GET, "/employee/{employeeId}")
                        .hasAnyRole("GLOBAL_ADMINISTRATOR", "BRANCH_MANAGER")

                )
                .formLogin(customizer -> customizer
                        .authenticationSuccessHandler(new ServerAuthenticationSuccessHandler() {
                            @Override
                            public Mono<Void> onAuthenticationSuccess(
                                    WebFilterExchange webFilterExchange,
                                    Authentication authentication) {
                                return Mono.fromRunnable(() -> webFilterExchange
                                        .getExchange().getResponse()
                                        .setStatusCode(HttpStatus.OK));
                            }
                        })
                        .authenticationFailureHandler(
                                new ServerAuthenticationEntryPointFailureHandler(
                                        new HttpStatusServerEntryPoint(
                                                HttpStatus.UNAUTHORIZED)))

                        .authenticationEntryPoint(
                                new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))

                        .requiresAuthenticationMatcher(
                                new PathPatternParserServerWebExchangeMatcher("/signin",
                                        HttpMethod.POST))

                )
                .logout(customizer -> customizer
                        .logoutSuccessHandler(
                                new HttpStatusReturningServerLogoutSuccessHandler())
                        .logoutHandler(new WebSessionServerLogoutHandler())
                        .requiresLogout(new PathPatternParserServerWebExchangeMatcher(
                                "/signout", HttpMethod.GET)))
                .build();
    }
}
