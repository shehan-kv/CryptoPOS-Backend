package com.cryptopos.orgs.dto;

import java.util.List;

public record OrgDetailsResponse(
        Long id,
        String name,
        String currency,
        String country,
        boolean isActive,
        Long employees,
        List<BranchDetailsResponse> branches) {

}
