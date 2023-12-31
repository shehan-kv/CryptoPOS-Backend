package com.cryptopos.orgs.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.orgs.handler.BranchHandler;

@Configuration
public class BranchRoute {

    @Bean
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.POST("/branch"), handler::createBranch)
                .andRoute(RequestPredicates.GET("/branch/by-user"), handler::getBranchesByUser)
                .andRoute(RequestPredicates.GET("/branch/by-org/{orgId}"), handler::getBranchesByOrg)
                .andRoute(RequestPredicates.GET("/branch/currency/{branchId}"), handler::getBranchCurrency)
                .andRoute(RequestPredicates.PUT("/branch/{branchId}"), handler::updateBranch);
    }
}
