package com.cryptopos.orders.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.Decimal128;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cryptopos.orders.dto.Item;
import com.cryptopos.orders.dto.PaymentMethod;

@Document(collection = "orders")
public record Order(
		@Id String id,
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
