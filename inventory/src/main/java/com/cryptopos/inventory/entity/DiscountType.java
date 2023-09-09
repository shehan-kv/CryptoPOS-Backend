package com.cryptopos.inventory.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("discount_types")
public record DiscountType(
        @Id Long id,
        String type) {

}
