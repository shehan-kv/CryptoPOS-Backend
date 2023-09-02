package com.cryptopos.orgs.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("countries")
public record Country(
        @Id Long id,
        String code,
        String name) {
}
