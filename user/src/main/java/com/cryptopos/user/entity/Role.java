package com.cryptopos.user.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("roles")
public record Role(
        @Id UUID id,
        String name) {

}
