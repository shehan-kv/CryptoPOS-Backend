package com.cryptopos.inventory.dto;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

public record ItemResult(
        @Id Long id,
        String lookupCode,
        String description,
        BigDecimal price,
        BigDecimal tax,
        String taxType,
        BigDecimal discount,
        String discountType,
        Long inStock) {

}
