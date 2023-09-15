package com.cryptopos.orgs.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.orgs.dto.CreateOrgRequest;
import com.cryptopos.orgs.dto.OrgUpdateRequest;
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

    public Mono<ServerResponse> getOrgDetailsByUser(ServerRequest request) {
        return orgService
                .getOrgDetailsByUser(request.queryParam("page"), request.queryParam("size"))
                .flatMap(result -> ServerResponse.ok().bodyValue(result));

    }

    public Mono<ServerResponse> getOrgsByUser(ServerRequest request) {
        return orgService
                .getOrgsByUser(request.queryParam("page"), request.queryParam("size"))
                .flatMap(result -> ServerResponse.ok().bodyValue(result));

    }

    public Mono<ServerResponse> updateOrg(ServerRequest request) {
        return request
                .bodyToMono(OrgUpdateRequest.class)
                .flatMap(updateRequest -> orgService
                        .updateOrg(
                                Long.parseLong(request.pathVariable("orgId")),
                                updateRequest))
                .flatMap(result -> {
                    if (result.isAuthError()) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                    }

                    if (result.isSuccess()) {
                        return ServerResponse.ok().build();
                    }

                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}
