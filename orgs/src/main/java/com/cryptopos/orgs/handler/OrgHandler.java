package com.cryptopos.orgs.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.orgs.dto.CreateOrgRequest;
import com.cryptopos.orgs.service.org.OrgService;

import reactor.core.publisher.Mono;

@Component
public class OrgHandler {

    private final OrgService orgService;

    public OrgHandler(OrgService orgService) {
        this.orgService = orgService;
    }

    public Mono<ServerResponse> createOrg(ServerRequest request) {
        return request
                .bodyToMono(CreateOrgRequest.class)
                .flatMap(createRequest -> orgService.createOrg(createRequest))
                .flatMap(createResult -> ServerResponse.status(HttpStatus.CREATED).build())
                .onErrorResume(error -> ServerResponse.badRequest().build());
    }
}
