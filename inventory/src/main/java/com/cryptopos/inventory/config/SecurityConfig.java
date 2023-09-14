package com.cryptopos.inventory.config;

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
                        .pathMatchers(HttpMethod.POST, "/{branchId}")
                        .hasRole("GLOBAL_ADMINISTRATOR")

                        .pathMatchers(HttpMethod.PUT, "/update/{itemId}")
                        .hasAnyRole("GLOBAL_ADMINISTRATOR", "INVENTORY_MANAGER", "BRANCH_MANAGER")

                        .pathMatchers(HttpMethod.GET, "/{branchId}")
                        .authenticated())
                .formLogin(customizer -> customizer.disable())
                .httpBasic(customizer -> customizer.disable())
                .build();
    }
}
