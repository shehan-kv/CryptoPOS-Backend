package com.cryptopos.orgs.service.branch;

import org.springframework.stereotype.Service;

import com.cryptopos.orgs.dto.BranchCreateRequest;
import com.cryptopos.orgs.dto.BranchCreateResult;
import com.cryptopos.orgs.dto.BranchUpdateRequest;
import com.cryptopos.orgs.dto.BranchUpdateResult;

import reactor.core.publisher.Mono;

@Service
public interface BranchService {

    Mono<BranchCreateResult> createBranch(BranchCreateRequest createRequest);

    Mono<BranchUpdateResult> updateBranch(Long branchId, BranchUpdateRequest updateRequest);
}
