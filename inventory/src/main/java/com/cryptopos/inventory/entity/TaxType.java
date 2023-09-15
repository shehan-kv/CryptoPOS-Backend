package com.cryptopos.inventory.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("tax_types")
public record TaxType(
        @Id Long id,
        String type) {

}
