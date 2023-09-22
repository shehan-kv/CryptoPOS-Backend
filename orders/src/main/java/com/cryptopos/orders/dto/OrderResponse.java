package com.cryptopos.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.Decimal128;

public record OrderResponse(
		String id,
		Long userId,
		Long orgId,
		Long branchId,
		List<Item> items,
		LocalDateTime createdDate,
		Decimal128 subTotal,
		Long itemCount,
		Decimal128 totalTax,
		Decimal128 totalDiscount,
		PaymentMethod paymentMethod) {

}
