package com.cryptopos.orgs.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("branches")
public record Branch(
        @Id Long id,
        String location,
        boolean isActive,
        Long orgId) {
}
