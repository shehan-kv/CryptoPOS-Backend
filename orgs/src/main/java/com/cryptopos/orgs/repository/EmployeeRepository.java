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

    public Mono<Long> saveBranch(Long userId, Long branchId) {

        return databaseClient
                .sql("INSERT INTO employee_branches (employee_id, branch_id) VALUES (:userId, :branchId)")
                .bind("userId", userId)
                .bind("branchId", branchId)
                .fetch()
                .rowsUpdated();
    }

}
