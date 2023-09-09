package com.cryptopos.inventory.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.inventory.entity.DiscountType;

public interface DiscountTypeRepository extends ReactiveCrudRepository<DiscountType, Long> {

}
