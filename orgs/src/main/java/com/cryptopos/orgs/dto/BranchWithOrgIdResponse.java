package com.cryptopos.orgs.dto;

public record BranchWithOrgIdResponse(
        Long orgId,
        Long id,
        String location) {

}
