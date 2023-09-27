package com.cryptopos.orders.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.cryptopos.orders.dto.MetricsResponse;
import com.cryptopos.orders.dto.OrderResponse;
import com.cryptopos.orders.entity.Order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

	@Aggregation(pipeline = {
			"{$match: { 'branchId': ?0, 'userId': ?1 }}",
			"{$sort: { 'createdDate': -1 }}",
			"{$limit: 20 }" })
	Flux<OrderResponse> findLastOrdersByUser(Long branchId, Long userId);

	@Aggregation(pipeline = {
			"{$match: { 'branchId': ?0 }}",
			"{$sort: { 'createdDate': -1 }}",
			"{$skip: ?1}",
			"{$limit: ?2 }" })
	Flux<OrderResponse> findAllByBranchId(Long branchId, Long offset, Long pageSize);

	@Aggregation(pipeline = {
			"{$match: { 'branchId': ?0 }}",
			"{$count: 'total' }"
	})
	Mono<Long> countAllByBranchId(Long branchId);

	@Aggregation(pipeline = {
			"{'$match': {'orgId': ?0, 'createdDate': {$gte: ?1}}}",
			"{'$group': {'_id': {'year': {'$year': '$createdDate'}, 'month': {'$month': '$createdDate'}}, " +
					"             'totalItemsSold': {'$sum': '$itemCount'}, " +
					"             'totalOrders': {'$sum': 1}, " +
					"             'totalRevenue': {'$sum': '$subTotal'}, " +
					"             'totalTax': {'$sum': '$totalTax'}, " +
					"             'totalDiscount': {'$sum': '$totalDiscount'}, " +
					"             'averageOrderValue': {'$avg': '$subTotal'}}}",
			"{'$group': {'_id': null, " +
					"             'totalItemsSold': {'$sum': '$totalItemsSold'}, " +
					"             'totalOrders': {'$sum': '$totalOrders'}, " +
					"             'totalRevenue': {'$sum': '$totalRevenue'}, " +
					"             'totalTax': {'$sum': '$totalTax'}, " +
					"             'totalDiscount': {'$sum': '$totalDiscount'}, " +
					"             'averageOrderValue': {'$avg': '$averageOrderValue'}, " +
					"             'monthlyRevenue': {" +
					"                   '$push': {" +
					"                       'month': '$_id.month', " +
					"                       'revenue': '$totalRevenue'" +
					"                   }" +
					"             }" +
					"           }}"
	})
	Mono<MetricsResponse> calculateMetricsForPastYear(Long orgId, LocalDateTime oneYearAgo);
}
