package com.cryptopos.inventory.dto;

import java.math.BigDecimal;

public record ItemCreateRequest(
        String lookupCode,
        String description,
        BigDecimal price,
        BigDecimal tax,
        String taxType,
        BigDecimal discount,
        String discountType,
        Long inStock) {

}
