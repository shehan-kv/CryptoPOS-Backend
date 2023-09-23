package com.cryptopos.orders.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.cryptopos.orders.dto.OrderResponse;
import com.cryptopos.orders.entity.Order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    @Aggregation(pipeline = {
            "{$match: { 'branchId': ?0, 'userId': ?1 }}",
            "{$sort: { 'createdDate': -1 }}",
            "{$limit: 20 }" })
    Flux<OrderResponse> findLastOrdersByUser(Long branchId, Long userId);

    @Aggregation(pipeline = {
            "{$match: { 'branchId': ?0 }}",
            "{$sort: { 'createdDate': -1 }}",
            "{$skip: ?1}",
            "{$limit: ?2 }" })
    Flux<OrderResponse> findAllByBranchId(Long branchId, Long offset, Long pageSize);

    @Aggregation(pipeline = {
            "{$match: { 'branchId': ?0 }}",
            "{$count: 'total' }"
    })
    Mono<Long> countAllByBranchId(Long branchId);
}
