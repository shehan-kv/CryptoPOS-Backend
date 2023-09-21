package com.cryptopos.orders.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.orders.dto.OrderCreateRequest;
import com.cryptopos.orders.exceptions.NoItemsException;
import com.cryptopos.orders.exceptions.NotPermittedException;
import com.cryptopos.orders.service.order.OrderService;

import reactor.core.publisher.Mono;

@Component
public class OrderHandler {

    private final OrderService orderService;

    public OrderHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    public Mono<ServerResponse> createOrder(ServerRequest request) {
        return request.bodyToMono(OrderCreateRequest.class)
                .flatMap(createRequest -> orderService.createOrder(createRequest))
                .flatMap(result -> ServerResponse.ok().build())
                .onErrorResume(error -> {
                    if (error instanceof NotPermittedException) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                    }

                    if (error instanceof NoItemsException) {
                        return ServerResponse.badRequest().build();
                    }

                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}