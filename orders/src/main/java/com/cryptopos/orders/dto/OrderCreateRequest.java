package com.cryptopos.orders.dto;

import java.util.List;

import org.bson.types.Decimal128;

public record OrderCreateRequest(
        List<Item> items,
        Decimal128 subTotal,
        Long itemCount,
        Decimal128 totalTax,
        Decimal128 totalDiscount,
        PaymentMethod paymentMethod) {

}
