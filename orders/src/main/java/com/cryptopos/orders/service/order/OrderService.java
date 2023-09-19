package com.cryptopos.orders.service.order;

import org.springframework.stereotype.Service;

import com.cryptopos.orders.dto.OrderCreateRequest;

import reactor.core.publisher.Mono;

@Service
public interface OrderService {

    Mono<Boolean> createOrder(OrderCreateRequest createRequest);
}
