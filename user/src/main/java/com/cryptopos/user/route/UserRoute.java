package com.cryptopos.user.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.user.handler.UserHandler;

@Configuration
public class UserRoute {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.POST("/signup"), handler::signUp)
                .andRoute(RequestPredicates.POST("/employee"), handler::createEmployee)
                .andRoute(RequestPredicates.GET("/employee"), handler::getEmployees)
                .andRoute(RequestPredicates.PUT("/employee/{employeeId}"), handler::updateEmployee)
                .andRoute(RequestPredicates.GET("/employee/{employeeId}"), handler::getEmployee);
    }
}
