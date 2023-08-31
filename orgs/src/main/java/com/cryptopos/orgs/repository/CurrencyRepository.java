package com.cryptopos.orgs.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.entity.Currency;

public interface CurrencyRepository extends ReactiveCrudRepository<Currency, Long> {

}
