package com.cryptopos.orgs.service.org;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cryptopos.orgs.dto.CreateOrgRequest;
import com.cryptopos.orgs.dto.OrgDetailsResponse;
import com.cryptopos.orgs.dto.OrgResponse;
import com.cryptopos.orgs.dto.OrgUpdateRequest;
import com.cryptopos.orgs.dto.OrgUpdateResult;
import com.cryptopos.orgs.dto.Page;

import reactor.core.publisher.Mono;

@Service
public interface OrgService {

    Mono<Boolean> createOrg(CreateOrgRequest createRequest);

    Mono<Page<OrgDetailsResponse>> getOrgDetailsByUser(Optional<String> pageNum, Optional<String> pageSize);

    Mono<Page<OrgResponse>> getOrgsByUser(Optional<String> pageNum, Optional<String> pageSize);

    Mono<OrgUpdateResult> updateOrg(Long orgId, OrgUpdateRequest updateRequest);
}
