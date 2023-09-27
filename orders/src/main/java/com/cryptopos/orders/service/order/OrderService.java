package com.cryptopos.orders.service.order;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cryptopos.orders.dto.MetricsResponse;
import com.cryptopos.orders.dto.OrderCreateRequest;
import com.cryptopos.orders.dto.OrderResponse;
import com.cryptopos.orders.dto.Page;

import reactor.core.publisher.Mono;

@Service
public interface OrderService {

    Mono<Boolean> createOrder(Long branchId, OrderCreateRequest createRequest);

    Mono<List<OrderResponse>> getLastOrdersByUser(Long branchId);

    Mono<Page<OrderResponse>> getOrdersByBranchId(Long branchId, Optional<String> pageNum, Optional<String> pageSize);

    Mono<MetricsResponse> getMetricsByOrgId(Long orgId);
}
