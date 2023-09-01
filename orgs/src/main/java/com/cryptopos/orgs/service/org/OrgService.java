package com.cryptopos.orgs.service.org;

import org.springframework.stereotype.Service;

import com.cryptopos.orgs.dto.CreateOrgRequest;

import reactor.core.publisher.Mono;

@Service
public interface OrgService {

    Mono<Boolean> createOrg(CreateOrgRequest createRequest);
}
