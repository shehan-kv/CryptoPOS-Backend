package com.cryptopos.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("roles")
public record Role(
        @Id Long id,
        String name) {

}
