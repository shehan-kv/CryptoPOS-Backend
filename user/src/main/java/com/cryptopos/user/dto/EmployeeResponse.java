package com.cryptopos.user.dto;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        boolean isActive,
        boolean isVerified,
        String role) {

}
