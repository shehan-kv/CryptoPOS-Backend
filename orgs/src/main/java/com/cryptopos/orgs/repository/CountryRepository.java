package com.cryptopos.orgs.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.entity.Country;

import reactor.core.publisher.Mono;

public interface CountryRepository extends ReactiveCrudRepository<Country, Long> {

    Mono<Country> findByCode(String code);
}
