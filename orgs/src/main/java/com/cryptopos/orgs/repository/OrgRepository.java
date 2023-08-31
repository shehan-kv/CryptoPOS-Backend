package com.cryptopos.orgs.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.entity.Org;

public interface OrgRepository extends ReactiveCrudRepository<Org, Long> {

}
