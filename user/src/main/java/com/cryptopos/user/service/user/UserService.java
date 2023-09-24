package com.cryptopos.user.service.user;

import org.springframework.stereotype.Service;

import com.cryptopos.user.dto.EmployeeCreateRequest;
import com.cryptopos.user.dto.EmployeeUpdateRequest;
import com.cryptopos.user.dto.SignUpRequest;

import reactor.core.publisher.Mono;

@Service
public interface UserService {

    Mono<Boolean> signUp(SignUpRequest signUpRequest);

    Mono<Boolean> createEmployee(EmployeeCreateRequest createRequest);

    Mono<Boolean> updateEmployee(Long employeeId, EmployeeUpdateRequest updateRequest);
}
