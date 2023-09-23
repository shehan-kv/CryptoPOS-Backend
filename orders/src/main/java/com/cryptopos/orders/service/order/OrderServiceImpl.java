package com.cryptopos.orders.service.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cryptopos.orders.dto.OrderCreateRequest;
import com.cryptopos.orders.dto.OrderResponse;
import com.cryptopos.orders.dto.Page;
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

    @Override
    public Mono<Page<OrderResponse>> getOrdersByBranchId(Long branchId, Optional<String> pageNum,
            Optional<String> pageSize) {

        Long pageNumLong = Math.max(Long.parseLong(pageNum.orElse("1")), 1);
        Long pageSizeLong = Math.max(Long.parseLong(pageSize.orElse("20")), 1);
        Long offset = (pageNumLong - 1) * pageSizeLong;

        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> {
                    return amqpService.getUserBranches(Long.parseLong(userId));
                })
                .map(branchList -> {
                    if (!branchList.contains(branchId)) {
                        throw new NotPermittedException();
                    }

                    return branchList;
                })
                .flatMap(branchList -> {
                    return orderRepository.findAllByBranchId(branchId, offset, pageSizeLong).collectList();
                })
                .zipWith(orderRepository.countAllByBranchId(branchId))
                .map(tuple -> {
                    var orderList = tuple.getT1();
                    Long pageCount = (long) Math.max((int) (Math.ceil(tuple.getT2()) / pageSizeLong), 1);

                    return new Page<OrderResponse>(pageNumLong, pageSizeLong, pageCount, orderList);
                });
    }

}
