package com.cryptopos.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(
        @Id Long id,
        String firstName,
        String lastName,
        String email,
        String password,
        boolean isActive,
        boolean isVerified,
        Long roleId) {
}
