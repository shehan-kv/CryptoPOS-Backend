package com.cryptopos.orgs.repository;

import java.util.Collection;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.dto.BranchResponse;
import com.cryptopos.orgs.dto.BranchCurrencyResponse;
import com.cryptopos.orgs.dto.BranchDetailsResponse;
import com.cryptopos.orgs.entity.Branch;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository extends ReactiveCrudRepository<Branch, Long> {

    Flux<BranchDetailsResponse> findAllByOrgIdIn(Collection<Long> ids);

    @Modifying
    @Query("UPDATE branches SET is_active = false WHERE org_id = :orgId")
    Mono<Long> disableBranches(Long orgId);

    @Modifying
    @Query("UPDATE branches SET location = :location, is_active = :isActive WHERE id = :id")
    Mono<Long> updateBranch(
            @Param("id") Long id,
            @Param("location") String location,
            @Param("isActive") boolean isActive);

    @Query("""
            SELECT b.id, b.location
            FROM branches b
            JOIN employee_branches eb ON eb.branch_id = b.id
            WHERE org_id = :orgId AND eb.employee_id = :userId
            OFFSET :offset
            LIMIT :pageSize
                """)
    Flux<BranchResponse> findByOrgIdAndUserId(Long orgId, Long userId, Long offset, Long pageSize);

    @Query("""
            SELECT COUNT(DISTINCT b.id)
            FROM branches b
            INNER JOIN employee_branches eb ON eb.branch_id = b.id
            WHERE b.org_id = :orgId AND eb.employee_id = :userId
            """)
    Mono<Long> countBranchesByOrgIdAndUserId(Long orgId, Long userId);

    @Query("""
            SELECT b.id, c.code as code
            FROM currencies c
            JOIN orgs o ON o.currency_id = c.id
            JOIN branches b ON b.org_id = o.id
            WHERE b.id = :branchId
                """)
    Mono<BranchCurrencyResponse> findCurrencyById(Long branchId);
}
