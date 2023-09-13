package com.cryptopos.orgs.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.dto.OrgDetailsResult;
import com.cryptopos.orgs.dto.OrgResponse;
import com.cryptopos.orgs.entity.Org;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrgRepository extends ReactiveCrudRepository<Org, Long> {

    @Query("""
            SELECT o.id, o.name, o.is_active, co.name as country,
            cu.code as currency, COUNT(DISTINCT eb.employee_id) as employees
            FROM orgs o
            JOIN countries co ON o.country_id = co.id
            JOIN currencies cu ON o.currency_id = cu.id
            LEFT JOIN branches b ON o.id = b.org_id
            LEFT JOIN employee_branches eb ON b.id = eb.branch_id
            WHERE o.id IN (
                SELECT DISTINCT b.org_id
                FROM branches b
                INNER JOIN employee_branches eb ON eb.branch_id = b.id
                WHERE eb.employee_id = :userId
                OFFSET :offset
                LIMIT :pageSize
            )
            GROUP BY o.id, o.name, o.is_active, co.name, cu.code;
                """)
    Flux<OrgDetailsResult> findOrgsDetailsByUser(Long userId, Long offset, Long pageSize);

    @Query("""
            SELECT o.id, o.name,
            FROM orgs o
            WHERE o.id IN (
                SELECT DISTINCT b.org_id
                FROM branches b
                INNER JOIN employee_branches eb ON eb.branch_id = b.id
                WHERE eb.employee_id = :userId
                OFFSET :offset
                LIMIT :pageSize
            )
                """)
    Flux<OrgResponse> findOrgsByUser(Long userId, Long offset, Long pageSize);

    @Query("""
            SELECT COUNT(DISTINCT b.org_id)
            FROM branches b
            INNER JOIN employee_branches eb ON eb.branch_id = b.id
            WHERE eb.employee_id = :userId
            """)
    Mono<Long> countOrgsByUser(Long userId);

    @Modifying
    @Query("UPDATE orgs SET name = :name, is_active = :isActive WHERE id = :id")
    Mono<Long> updateOrg(@Param("id") Long id, @Param("name") String name, @Param("isActive") boolean isActive);

    @Query("""
            SELECT DISTINCT b.org_id
            FROM branches b
            INNER JOIN employee_branches eb ON eb.branch_id = b.id
            WHERE eb.employee_id = :userId
            """)
    Flux<Long> findOrgIdsByUser(Long userId);
}
