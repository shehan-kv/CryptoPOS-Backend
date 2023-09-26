package com.cryptopos.user.service.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cryptopos.user.dto.EmployeeCreateRequest;
import com.cryptopos.user.dto.EmployeeResponse;
import com.cryptopos.user.dto.EmployeeWithBranchesResponse;
import com.cryptopos.user.dto.EmployeeUpdateRequest;
import com.cryptopos.user.dto.Page;
import com.cryptopos.user.dto.SignUpRequest;

import reactor.core.publisher.Mono;

@Service
public interface UserService {

    Mono<Boolean> signUp(SignUpRequest signUpRequest);

    Mono<Boolean> createEmployee(EmployeeCreateRequest createRequest);

    Mono<Boolean> updateEmployee(Long employeeId, EmployeeUpdateRequest updateRequest);

    Mono<EmployeeWithBranchesResponse> getEmployee(Long employeeId);

    Mono<Page<EmployeeResponse>> getEmployees(Optional<String> pageNum, Optional<String> pageSize);
}
