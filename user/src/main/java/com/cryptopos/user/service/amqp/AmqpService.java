package com.cryptopos.user.service.amqp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public interface AmqpService {

    Mono<List<Long>> getUserBranches(Long userId);

    Mono<Boolean> setUserBranches(Map<Long, List<Long>> request);

    Mono<HashMap<Long, List<Long>>> getEmployeeBranchInfo(List<Long> request);
}
