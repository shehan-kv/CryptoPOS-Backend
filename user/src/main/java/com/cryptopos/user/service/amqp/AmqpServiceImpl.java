package com.cryptopos.user.service.amqp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Mono<Boolean> setUserBranches(Map<Long, List<Long>> request) {
        return Mono.fromCallable(() -> {
            rabbitTemplate.getRabbitTemplate().convertAndSend("pos.exchange", "branches.add", request);
            return true;
        });
    }

    @Override
    public Mono<HashMap<Long, List<Long>>> getEmployeeBranchInfo(List<Long> request) {
        RabbitConverterFuture<HashMap<Long, List<Long>>> result = rabbitTemplate
                .convertSendAndReceive("pos.exchange", "employee.info", request);

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
