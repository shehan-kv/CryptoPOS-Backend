package com.cryptopos.inventory.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .formLogin(customizer -> customizer.disable())
                .httpBasic(customizer -> customizer.disable())
                .build();
    }
}
