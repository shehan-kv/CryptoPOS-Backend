package com.cryptopos.orgs.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.entity.Branch;

public interface BranchRepository extends ReactiveCrudRepository<Branch, Long> {

}
