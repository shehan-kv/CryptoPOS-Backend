package com.cryptopos.orgs.service.amqp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.cryptopos.orgs.repository.BranchRepository;
import com.cryptopos.orgs.repository.EmployeeRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AMQPService {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;

    public AMQPService(EmployeeRepository empRepository, BranchRepository brRepository) {
        this.employeeRepository = empRepository;
        this.branchRepository = brRepository;
    }

    @RabbitListener(queues = "user.branches")
    public Mono<List<Long>> getBranches(Long data) {
        return employeeRepository.getBranches(data).collectList();
    }

    @RabbitListener(queues = "branch.org")
    public Mono<Long> getBranchOrgId(Long data) {
        return branchRepository.findOrgIdByBranchId(data);
    }

    @RabbitListener(queues = "branches.add")
    public Mono<Void> addUserBranches(Map<Long, List<Long>> data) {

        Long userId = data.keySet().iterator().next();
        List<Long> branches = data.values().iterator().next();

        return employeeRepository
                .deleteByUserId(userId)
                .flatMap(result -> {
                    return Flux.fromIterable(branches)
                            .flatMap(branchId -> {
                                return employeeRepository.saveBranch(userId, branchId);
                            })
                            .collectList();
                })
                .flatMap(result -> Mono.empty());
    }

    @RabbitListener(queues = "employee.info")
    public Mono<HashMap<Long, List<Long>>> getEmployeeInfo(List<Long> data) {

        Long userId = data.get(0);
        Long offset = data.get(1);
        Long limit = data.get(2);

        return employeeRepository
                .getBranches(userId).collectList()
                .flatMap(branchList -> {
                    return employeeRepository
                            .getEmployeeIdsByBranches(branchList, userId, offset, limit)
                            .collectList()
                            .zipWith(employeeRepository.countEmployeeIdsByBranches(branchList, userId))
                            .map(tuple -> {
                                return tuple;
                            });

                })
                .map(tuple -> {

                    HashMap<Long, List<Long>> resultMap = new HashMap<>();
                    resultMap.put(tuple.getT2(), tuple.getT1());
                    return resultMap;
                });
    }
}
