package com.cryptopos.orders.dto;

import java.util.List;

import org.bson.types.Decimal128;

public record MetricsResponse(
        Long totalItemsSold,
        Long totalOrders,
        Decimal128 totalRevenue,
        Decimal128 totalTax,
        Decimal128 totalDiscount,
        Decimal128 averageOrderValue,
        List<MonthlyRevenue> monthlyRevenue) {
}
