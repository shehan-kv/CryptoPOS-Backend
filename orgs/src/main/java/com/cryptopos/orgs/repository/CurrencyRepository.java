package com.cryptopos.orgs.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.entity.Currency;

import reactor.core.publisher.Mono;

public interface CurrencyRepository extends ReactiveCrudRepository<Currency, Long> {

    Mono<Currency> findByCode(String code);
}
