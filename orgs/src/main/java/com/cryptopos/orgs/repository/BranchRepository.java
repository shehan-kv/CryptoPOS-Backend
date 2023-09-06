package com.cryptopos.orgs.repository;

import java.util.Collection;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.dto.BranchResult;
import com.cryptopos.orgs.entity.Branch;

import reactor.core.publisher.Flux;

public interface BranchRepository extends ReactiveCrudRepository<Branch, Long> {

    Flux<BranchResult> findAllByOrgIdIn(Collection<Long> ids);
}
