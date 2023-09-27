package com.cryptopos.orgs.service.branch;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cryptopos.orgs.dto.BranchCreateRequest;
import com.cryptopos.orgs.dto.BranchCreateResult;
import com.cryptopos.orgs.dto.BranchCurrencyResponse;
import com.cryptopos.orgs.dto.BranchResponse;
import com.cryptopos.orgs.dto.BranchUpdateRequest;
import com.cryptopos.orgs.dto.BranchUpdateResult;
import com.cryptopos.orgs.dto.Page;
import com.cryptopos.orgs.dto.UserBranchResponse;

import reactor.core.publisher.Mono;

@Service
public interface BranchService {

    Mono<BranchCreateResult> createBranch(BranchCreateRequest createRequest);

    Mono<BranchUpdateResult> updateBranch(Long branchId, BranchUpdateRequest updateRequest);

    Mono<Page<BranchResponse>> getBranchesByOrg(Long orgId, Optional<String> pageNum, Optional<String> pageSize);

    Mono<BranchCurrencyResponse> getBranchCurrency(Long branchId);

    Mono<List<UserBranchResponse>> getBranchesByUser();
}
