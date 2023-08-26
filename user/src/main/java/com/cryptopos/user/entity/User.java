package com.cryptopos.user.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(
        @Id UUID id,
        String firstName,
        String lastName,
        String email,
        String password,
        boolean isActive,
        boolean isVerified,
        UUID roleId) {
}
