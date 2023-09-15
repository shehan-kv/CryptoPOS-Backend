package com.cryptopos.orgs.dto;

public record BranchDetailsResponse(
        Long id,
        Long orgId,
        String location,
        boolean isActive) {

}
