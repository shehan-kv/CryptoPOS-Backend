package com.cryptopos.orgs.service.amqp;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.cryptopos.orgs.repository.BranchRepository;
import com.cryptopos.orgs.repository.EmployeeRepository;

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
}
