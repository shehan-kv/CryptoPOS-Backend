package com.cryptopos.orders.dto;

import java.time.LocalDateTime;

import org.bson.types.Decimal128;

public record OrderResponse(
		String id,
		Long userId,
		Long orgId,
		Long branchId,
		LocalDateTime createdDate,
		Decimal128 subTotal,
		Long itemCount,
		Decimal128 totalTax,
		Decimal128 totalDiscount,
		PaymentMethod paymentMethod) {

}
