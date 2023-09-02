package com.cryptopos.orgs.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("orgs")
public record Org(
        @Id Long id,
        String name,
        Long countryId,
        Long currencyId,
        boolean isActive) {
}
