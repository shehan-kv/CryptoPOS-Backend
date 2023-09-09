package com.cryptopos.inventory.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.inventory.entity.Item;

public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {

}
