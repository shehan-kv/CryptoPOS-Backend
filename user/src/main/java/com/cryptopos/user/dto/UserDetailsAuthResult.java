package com.cryptopos.user.dto;

public record UserDetailsAuthResult(
        Long id,
        String password,
        Boolean isActive,
        Boolean isVerified,
        String role) {

}
