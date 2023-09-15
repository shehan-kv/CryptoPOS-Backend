package com.cryptopos.orgs.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("currencies")
public record Currency(
        @Id Long id,
        String name,
        String code,
        String symbol) {
}
