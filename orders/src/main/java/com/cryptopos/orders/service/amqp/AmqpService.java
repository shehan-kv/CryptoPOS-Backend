package com.cryptopos.orders.service.amqp;

import java.util.List;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public interface AmqpService {

    Mono<List<Long>> getUserBranches(Long userId);

    Mono<List<Long>> getUserOrgs(Long userId);

    Mono<Long> getBranchOrgId(Long branchId);
}
