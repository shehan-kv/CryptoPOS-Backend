package com.cryptopos.orders.service.amqp;

import java.util.List;

import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitConverterFuture;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class AmqpServiceImpl implements AmqpService {

    private final AsyncRabbitTemplate rabbitTemplate;

    public AmqpServiceImpl(AsyncRabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Mono<List<Long>> getUserBranches(Long userId) {
        RabbitConverterFuture<List<Long>> result = rabbitTemplate
                .convertSendAndReceive("pos.exchange", "user.branches", userId);

        return Mono.fromFuture(() -> result)
                .flatMap(response -> {
                    if (response != null) {
                        return Mono.just(response);
                    } else {
                        return Mono.error(new RuntimeException("RabbitMQ response is null"));
                    }
                });
    }

    @Override
    public Mono<Long> getBranchOrgId(Long branchId) {

        RabbitConverterFuture<Long> result = rabbitTemplate
                .convertSendAndReceive("pos.exchange", "branch.org", branchId);

        return Mono.fromFuture(() -> result)
                .flatMap(response -> {
                    if (response != null) {
                        return Mono.just(response);
                    } else {
                        return Mono.error(new RuntimeException("RabbitMQ response is null"));
                    }
                });

    }

    @Override
    public Mono<List<Long>> getUserOrgs(Long userId) {
        RabbitConverterFuture<List<Long>> result = rabbitTemplate
                .convertSendAndReceive("pos.exchange", "user.orgs", userId);

        return Mono.fromFuture(() -> result)
                .flatMap(response -> {
                    if (response != null) {
                        return Mono.just(response);
                    } else {
                        return Mono.error(new RuntimeException("RabbitMQ response is null"));
                    }
                });
    }
}
