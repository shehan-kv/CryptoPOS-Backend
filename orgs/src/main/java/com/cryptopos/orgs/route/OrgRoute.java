package com.cryptopos.orgs.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.orgs.handler.OrgHandler;

@Configuration
public class OrgRoute {

    @Bean
    public RouterFunction<ServerResponse> orgRoutes(OrgHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.POST("/"), handler::createOrg)
                .andRoute(RequestPredicates.GET("/"), handler::getOrgDetailsByUser)
                .andRoute(RequestPredicates.PUT("/{orgId}"), handler::updateOrg);
    }
}
