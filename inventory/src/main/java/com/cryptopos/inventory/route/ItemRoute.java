package com.cryptopos.inventory.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.inventory.handler.ItemHandler;

@Configuration
public class ItemRoute {

    @Bean
    public RouterFunction<ServerResponse> branchRoutes(ItemHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.POST("/{branchId}"), handler::createItem)
                .andRoute(RequestPredicates.PUT("/update/{itemId}"), handler::updateItem)
                .andRoute(RequestPredicates.PUT("/update-stock/{itemId}"), handler::updateItemStock)
                .andRoute(RequestPredicates.GET("/{branchId}"), handler::getItemsByBranch)
                .andRoute(RequestPredicates.GET("/{branchId}/{lookupCode}"), handler::getItemsByBranchAndLookupCode);

    }
}