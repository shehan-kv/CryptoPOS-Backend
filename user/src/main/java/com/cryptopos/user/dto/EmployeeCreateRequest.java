package com.cryptopos.user.dto;

import java.util.List;

public record EmployeeCreateRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        List<Long> branches,
        String role) {

}
