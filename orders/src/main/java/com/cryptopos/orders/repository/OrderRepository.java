package com.cryptopos.orders.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.cryptopos.orders.dto.OrderResponse;
import com.cryptopos.orders.entity.Order;

import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    @Aggregation(pipeline = {
            "{$match: { 'branchId': ?0, 'userId': ?1 }}",
            "{$sort: { 'createdDate': -1 }}",
            "{$limit: 20 }" })
    Flux<OrderResponse> findLastOrdersByUser(Long branchId, Long userId);
}
