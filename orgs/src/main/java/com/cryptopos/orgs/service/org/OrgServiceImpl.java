package com.cryptopos.orgs.service.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cryptopos.orgs.dto.BranchResult;
import com.cryptopos.orgs.dto.CreateOrgRequest;
import com.cryptopos.orgs.dto.OrgDetailsResponse;
import com.cryptopos.orgs.dto.OrgResult;
import com.cryptopos.orgs.dto.Page;
import com.cryptopos.orgs.repository.BranchRepository;
import com.cryptopos.orgs.repository.CountryRepository;
import com.cryptopos.orgs.repository.CurrencyRepository;
import com.cryptopos.orgs.repository.EmployeeRepository;
import com.cryptopos.orgs.repository.OrgRepository;
import com.cryptopos.orgs.entity.Branch;
import com.cryptopos.orgs.entity.Country;
import com.cryptopos.orgs.entity.Currency;
import com.cryptopos.orgs.entity.Org;

import reactor.core.publisher.Mono;

@Component
public class OrgServiceImpl implements OrgService {

    private final OrgRepository orgRepository;
    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;
    private final CountryRepository countryRepository;
    private final CurrencyRepository currencyRepository;

    public OrgServiceImpl(OrgRepository orgRepo,
            BranchRepository branchRepo,
            EmployeeRepository empRepo,
            CountryRepository countryRepo,
            CurrencyRepository currencyRepo) {

        this.orgRepository = orgRepo;
        this.branchRepository = branchRepo;
        this.employeeRepository = empRepo;
        this.countryRepository = countryRepo;
        this.currencyRepository = currencyRepo;
    }

    @Override
    public Mono<Boolean> createOrg(CreateOrgRequest createRequest) {

        return currencyRepository
                .findByCode(createRequest.currency())
                .zipWith(countryRepository.findByCode(createRequest.country()))
                .flatMap(tuple -> {

                    Currency currency = tuple.getT1();
                    Country country = tuple.getT2();

                    Org newOrg = new Org(
                            null,
                            createRequest.name(),
                            country.id(),
                            currency.id(), true);

                    return orgRepository.save(newOrg);
                })
                .flatMap(savedOrg -> {
                    Branch newBranch = new Branch(
                            null,
                            createRequest.branchLocation(),
                            true,
                            savedOrg.id());

                    return branchRepository
                            .save(newBranch);
                })
                .zipWhen(branch -> {

                    return ReactiveSecurityContextHolder
                            .getContext()
                            .flatMap(context -> Mono.just(context.getAuthentication().getName()));

                })
                .flatMap(tuple -> {

                    Branch branch = tuple.getT1();
                    Long userId = Long.parseLong(tuple.getT2());

                    return employeeRepository
                            .saveBranch(userId, branch.id());

                })
                .flatMap(savedId -> Mono.just(true));

    }

    public Mono<Page<OrgDetailsResponse>> getOrgDetailsByUser(
            Optional<String> pageNum,
            Optional<String> pageSize) {

        int pageNumInt = Integer.parseInt(pageNum.orElse("1"));
        int pageSizeInt = Integer.parseInt(pageSize.orElse("20"));
        int offset = (pageNumInt - 1) * pageSizeInt;

        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> {
                    Long userIdLong = Long.parseLong(userId);

                    return orgRepository
                            .findOrgsByUser(userIdLong, offset, pageSizeInt)
                            .collectList();
                })
                .map(orgList -> {
                    var orgMap = new HashMap<Long, OrgDetailsResponse>();

                    for (OrgResult orgResult : orgList) {
                        orgMap.put(orgResult.id(), new OrgDetailsResponse(
                                orgResult.id(),
                                orgResult.name(),
                                orgResult.currency(),
                                orgResult.country(),
                                orgResult.isActive(),
                                orgResult.employees(),
                                new ArrayList<BranchResult>()));
                    }
                    return orgMap;
                })
                .zipWhen(orgList -> branchRepository.findAllByOrgIdIn(orgList.keySet()).collectList())
                .map(tuple -> {
                    var orgMap = tuple.getT1();
                    var branchList = tuple.getT2();

                    for (BranchResult branchResult : branchList) {
                        orgMap.get(branchResult.orgId()).branches().add(branchResult);
                    }

                    return orgMap.values();
                })
                .zipWith(ReactiveSecurityContextHolder
                        .getContext()
                        .map(context -> context.getAuthentication().getName())
                        .flatMap(userId -> {
                            Long userIdLong = Long.parseLong(userId);
                            return orgRepository.countOrgsByUser(userIdLong);
                        }))
                .map(tuple -> {
                    var orgList = tuple.getT1();
                    var pageCount = Math.max((int) (Math.ceil(tuple.getT2()) / pageSizeInt), 1);
                    return new Page<OrgDetailsResponse>(pageNumInt, pageSizeInt, pageCount, orgList);
                });
    }

}
