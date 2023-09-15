package com.cryptopos.orgs.repository;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
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

    public Flux<Long> getBranches(Long userId) {

        return databaseClient
                .sql("SELECT branch_id FROM employee_branches WHERE employee_id = :userId")
                .bind("userId", userId)
                .fetch()
                .all()
                .map(row -> (Long) row.get("branch_id"));
    }

    public Mono<Long> countBranches(Long userId) {

        return databaseClient
                .sql("SELECT COUNT(DISTINCT branch_id) FROM employee_branches WHERE employee_id = :userId")
                .bind("userId", userId)
                .fetch()
                .one()
                .map(row -> (Long) row.get("branch_id"));

    }

}
