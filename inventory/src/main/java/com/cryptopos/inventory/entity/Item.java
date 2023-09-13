package com.cryptopos.inventory.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("items")
public record Item(
        @Id Long id,
        String lookupCode,
        String description,
        BigDecimal price,
        BigDecimal tax,
        Long taxType,
        BigDecimal discount,
        Long discountType,
        Long inStock,
        Long branchId,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate) {

}
