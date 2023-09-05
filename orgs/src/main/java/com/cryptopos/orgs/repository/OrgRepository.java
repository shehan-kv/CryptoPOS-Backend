package com.cryptopos.orgs.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.orgs.dto.OrgResult;
import com.cryptopos.orgs.entity.Org;

import reactor.core.publisher.Flux;

public interface OrgRepository extends ReactiveCrudRepository<Org, Long> {

    @Query("""
            SELECT o.id, o.name, o.is_active, co.name as country,
            cu.code as currency
            FROM orgs o
            JOIN countries co ON o.country_id = co.id
            JOIN currencies cu ON o.currency_id = cu.id
            WHERE o.id IN (
                SELECT DISTINCT b.org_id
                FROM branches b
                INNER JOIN employee_branches eb ON eb.branch_id = b.id
                WHERE eb.employee_id = :userId
                OFFSET :offset
                LIMIT :pageSize
            )
                """)
    Flux<OrgResult> findOrgsByUser(Long userId, int offset, int pageSize);
}
