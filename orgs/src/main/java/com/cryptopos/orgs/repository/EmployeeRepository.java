package com.cryptopos.orgs.repository;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class EmployeeRepository {

    private final DatabaseClient databaseClient;

    public EmployeeRepository(DatabaseClient dClient) {
        this.databaseClient = dClient;
    }

    public Mono<Long> saveOrg(Long userId, Long orgId) {

        return databaseClient
                .sql("INSERT INTO employee_orgs (employee_id, org_id) VALUES (:userId, :orgId)")
                .bind("userId", userId)
                .bind("orgId", orgId)
                .fetch()
                .rowsUpdated();
    }

}
