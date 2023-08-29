package com.cryptopos.user.dto;

public record UserDetailsAuthResult(
        String email,
        String password,
        Boolean isActive,
        Boolean isVerified,
        String role) {

}
