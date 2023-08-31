package com.cryptopos.orgs.repository;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRepository {

    private final DatabaseClient databaseClient;

    public EmployeeRepository(DatabaseClient dClient) {
        this.databaseClient = dClient;
    }

}
