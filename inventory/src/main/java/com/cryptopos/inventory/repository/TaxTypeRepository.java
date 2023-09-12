package com.cryptopos.inventory.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.inventory.entity.TaxType;

import reactor.core.publisher.Mono;

public interface TaxTypeRepository extends ReactiveCrudRepository<TaxType, Long> {

    Mono<TaxType> findByType(String type);
}
