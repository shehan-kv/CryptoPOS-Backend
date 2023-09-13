package com.cryptopos.inventory.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.inventory.entity.DiscountType;

import reactor.core.publisher.Mono;

public interface DiscountTypeRepository extends ReactiveCrudRepository<DiscountType, Long> {

    Mono<DiscountType> findByType(String type);
}
