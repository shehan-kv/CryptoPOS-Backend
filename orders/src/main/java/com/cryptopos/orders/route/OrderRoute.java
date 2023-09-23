package com.cryptopos.orders.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.orders.handler.OrderHandler;

@Configuration
public class OrderRoute {

    @Bean
    public RouterFunction<ServerResponse> branchRoutes(OrderHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.POST("/{branchId}"), handler::createOrder)
                .andRoute(RequestPredicates.GET("/{branchId}"), handler::getOrdersByBranchId)
                .andRoute(RequestPredicates.GET("/user-last-orders/{branchId}"), handler::getLastOrdersByUser);

    }
}
