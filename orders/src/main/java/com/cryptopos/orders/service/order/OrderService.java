package com.cryptopos.orders.service.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cryptopos.orders.dto.OrderCreateRequest;
import com.cryptopos.orders.dto.OrderResponse;

import reactor.core.publisher.Mono;

@Service
public interface OrderService {

    Mono<Boolean> createOrder(Long branchId, OrderCreateRequest createRequest);

    Mono<List<OrderResponse>> getLastOrdersByUser(Long branchId);
}
