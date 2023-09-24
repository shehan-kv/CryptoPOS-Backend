package com.cryptopos.user.dto;

import java.util.List;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        boolean isActive,
        List<Long> branches,
        String role) {

}
