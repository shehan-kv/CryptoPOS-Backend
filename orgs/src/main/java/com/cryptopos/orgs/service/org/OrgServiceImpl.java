package com.cryptopos.orgs.service.org;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cryptopos.orgs.dto.CreateOrgRequest;
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

}
