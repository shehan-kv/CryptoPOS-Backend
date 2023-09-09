package com.cryptopos.inventory.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.inventory.entity.TaxType;

public interface TaxTypeRepository extends ReactiveCrudRepository<TaxType, Long> {

}
