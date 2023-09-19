package com.cryptopos.orders.dto;

import org.bson.types.Decimal128;

public record Item(
        Long id,
        String lookupCode,
        String description,
        Long qty,
        Decimal128 price,
        Decimal128 tax,
        String taxType,
        Decimal128 discount,
        String discountType,
        Decimal128 totalPrice,
        Decimal128 totalDiscount,
        Decimal128 totalTax) {

}
