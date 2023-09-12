package com.cryptopos.orgs.service.amqp;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptopos.orgs.repository.EmployeeRepository;

import reactor.core.publisher.Mono;

@Service
public class AMQPService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @RabbitListener(queues = "user.branches")
    public Mono<List<Long>> getBranches(Long data) {
        return employeeRepository.getBranches(data).collectList();
    }
}
