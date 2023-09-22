package com.cryptopos.orders.service.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cryptopos.orders.dto.OrderCreateRequest;
import com.cryptopos.orders.dto.OrderResponse;
import com.cryptopos.orders.entity.Order;
import com.cryptopos.orders.exceptions.NoItemsException;
import com.cryptopos.orders.exceptions.NotPermittedException;
import com.cryptopos.orders.repository.OrderRepository;
import com.cryptopos.orders.service.amqp.AmqpService;

import reactor.core.publisher.Mono;

@Component
public class OrderServiceImpl implements OrderService {

    private final AmqpService amqpService;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(AmqpService amqpService, OrderRepository orderRepository) {
        this.amqpService = amqpService;
        this.orderRepository = orderRepository;
    }

    @Override
    public Mono<Boolean> createOrder(Long branchId, OrderCreateRequest createRequest) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> {
                    return amqpService.getUserBranches(Long.parseLong(userId))
                            .map(branchList -> {
                                if (!branchList.contains(branchId)) {
                                    throw new NotPermittedException();
                                }

                                if (createRequest.items().size() == 0) {
                                    throw new NoItemsException();
                                }
                                return userId;
                            });
                })
                .zipWith(amqpService.getBranchOrgId(branchId))
                .flatMap(tuple -> {
                    Long userId = Long.parseLong(tuple.getT1());
                    Long orgId = tuple.getT2();

                    Order newOrder = new Order(
                            null,
                            userId,
                            orgId,
                            branchId,
                            createRequest.items(),
                            LocalDateTime.now(),
                            createRequest.subTotal(),
                            createRequest.itemCount(),
                            createRequest.totalTax(),
                            createRequest.totalDiscount());

                    return orderRepository.save(newOrder).map(result -> true);
                });
    }

    @Override
    public Mono<List<OrderResponse>> getLastOrdersByUser(Long branchId) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> {
                    Long userIdLong = Long.parseLong(userId);

                    return amqpService.getUserBranches(userIdLong)
                            .map(branchList -> {
                                if (!branchList.contains(branchId)) {
                                    throw new NotPermittedException();
                                }

                                return branchList;
                            })
                            .flatMap(branchList -> orderRepository.findLastOrdersByUser(branchId,
                                    userIdLong).collectList());

                });
    }

}
