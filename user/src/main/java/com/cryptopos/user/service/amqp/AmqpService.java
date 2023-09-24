package com.cryptopos.user.service.amqp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cryptopos.user.dto.EmployeeBranchesAddRequest;

import reactor.core.publisher.Mono;

@Service
public interface AmqpService {

    Mono<List<Long>> getUserBranches(Long userId);

    Mono<Boolean> setUserBranches(EmployeeBranchesAddRequest request);
}
