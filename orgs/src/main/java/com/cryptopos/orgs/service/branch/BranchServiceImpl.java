package com.cryptopos.orgs.service.branch;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cryptopos.orgs.dto.BranchCreateRequest;
import com.cryptopos.orgs.dto.BranchCreateResult;
import com.cryptopos.orgs.dto.BranchUpdateRequest;
import com.cryptopos.orgs.dto.BranchUpdateResult;
import com.cryptopos.orgs.entity.Branch;
import com.cryptopos.orgs.repository.BranchRepository;
import com.cryptopos.orgs.repository.EmployeeRepository;
import com.cryptopos.orgs.repository.OrgRepository;

import reactor.core.publisher.Mono;

@Component
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final OrgRepository orgRepository;
    private final EmployeeRepository employeeRepository;

    public BranchServiceImpl(BranchRepository branchRepo, OrgRepository orgRepo, EmployeeRepository empRepo) {
        this.branchRepository = branchRepo;
        this.orgRepository = orgRepo;
        this.employeeRepository = empRepo;
    }

    @Override
    public Mono<BranchCreateResult> createBranch(BranchCreateRequest createRequest) {

        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .zipWhen(userId -> orgRepository.findOrgIdsByUser(Long.parseLong(userId)).collectList())
                .flatMap(tuple -> {

                    var orgIdList = tuple.getT2();
                    Long userId = Long.parseLong(tuple.getT1());

                    if (!orgIdList.contains(createRequest.orgId())) {
                        return Mono.just(new BranchCreateResult(false, true));
                    }

                    Branch newBranch = new Branch(
                            null,
                            createRequest.location(),
                            createRequest.isActive(),
                            createRequest.orgId());

                    return branchRepository
                            .save(newBranch)
                            .flatMap(savedEntity -> employeeRepository.saveBranch(userId, savedEntity.id()))
                            .map(result -> result > 0 ? new BranchCreateResult(true, false)
                                    : new BranchCreateResult(false, false));
                });
    }

    @Override
    public Mono<BranchUpdateResult> updateBranch(Long branchId, BranchUpdateRequest updateRequest) {

        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> employeeRepository.getBranches(Long.parseLong(userId)).collectList())
                .flatMap(branchList -> {

                    if (!branchList.contains(branchId)) {
                        return Mono.just(new BranchUpdateResult(false, true));
                    }

                    return branchRepository
                            .updateBranch(branchId, updateRequest.location(), updateRequest.isActive())
                            .map(result -> result > 0 ? new BranchUpdateResult(true, false)
                                    : new BranchUpdateResult(false, false));

                });
    }

}
