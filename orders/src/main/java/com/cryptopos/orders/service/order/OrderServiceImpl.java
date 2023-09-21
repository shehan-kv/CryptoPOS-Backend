package com.cryptopos.orders.service.order;

import java.time.LocalDateTime;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cryptopos.orders.dto.OrderCreateRequest;
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
                .flatMap(userId -> {

                    Order newOrder = new Order(
                            null,
                            Long.parseLong(userId),
                            branchId, // ORG ID FROM AMQP
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

}
