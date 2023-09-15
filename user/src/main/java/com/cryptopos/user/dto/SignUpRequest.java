package com.cryptopos.user.dto;

public record SignUpRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String confirmPassword) {
}
