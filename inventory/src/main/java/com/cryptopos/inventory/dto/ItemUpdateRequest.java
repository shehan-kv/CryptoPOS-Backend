package com.cryptopos.inventory.dto;

import java.math.BigDecimal;

public record ItemUpdateRequest(
        String lookupCode,
        String description,
        BigDecimal price,
        BigDecimal tax,
        String taxType,
        BigDecimal discount,
        String discountType) {

}
