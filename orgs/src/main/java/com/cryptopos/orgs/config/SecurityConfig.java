package com.cryptopos.orgs.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public MapReactiveUserDetailsService mapReactiveUserDetailsService() {
        return new MapReactiveUserDetailsService(Collections.emptyMap());
    }

    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(HttpMethod.POST, "/")
                        .hasRole("GLOBAL_ADMINISTRATOR")

                        .pathMatchers(HttpMethod.GET, "/")
                        .authenticated()

                )
                .formLogin(customizer -> customizer.disable())
                .httpBasic(customizer -> customizer.disable())
                .build();
    }
}
