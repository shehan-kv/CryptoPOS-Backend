package com.cryptopos.orgs.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.entity.Country;

public interface CountryRepository extends ReactiveCrudRepository<Country, Long> {

}
