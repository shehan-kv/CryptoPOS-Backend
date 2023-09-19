package com.cryptopos.orders.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.cryptopos.orders.entity.Order;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

}
