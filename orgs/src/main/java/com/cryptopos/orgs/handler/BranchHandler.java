package com.cryptopos.orgs.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.orgs.dto.BranchCreateRequest;
import com.cryptopos.orgs.service.branch.BranchService;

import reactor.core.publisher.Mono;

@Component
public class BranchHandler {

    private final BranchService branchService;

    public BranchHandler(BranchService branchService) {
        this.branchService = branchService;
    }

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        return request
                .bodyToMono(BranchCreateRequest.class)
                .flatMap(createRequest -> branchService.createBranch(createRequest))
                .flatMap(response -> {

                    if (response.isSuccess()) {
                        return ServerResponse.status(HttpStatus.CREATED).build();
                    }

                    if (response.isAuthError()) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                    }

                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}
