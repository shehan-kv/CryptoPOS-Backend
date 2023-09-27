package com.cryptopos.orders.dto;

import org.bson.types.Decimal128;

public record MonthlyRevenue(
        Integer month,
        Decimal128 revenue) {

}
