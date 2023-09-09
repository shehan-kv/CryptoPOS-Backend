package com.cryptopos.orgs.repository;

import java.util.Collection;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.dto.BranchResult;
import com.cryptopos.orgs.entity.Branch;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository extends ReactiveCrudRepository<Branch, Long> {

    Flux<BranchResult> findAllByOrgIdIn(Collection<Long> ids);

    @Modifying
    @Query("UPDATE branches SET is_active = false WHERE org_id = :orgId")
    Mono<Long> disableBranches(Long orgId);

    @Modifying
    @Query("UPDATE branches SET location = :location, is_active = :isActive WHERE id = :id")
    Mono<Long> updateBranch(
            @Param("id") Long id,
            @Param("location") String location,
            @Param("isActive") boolean isActive);
}
