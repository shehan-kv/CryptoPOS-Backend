package com.cryptopos.orgs.dto;

public record BranchCreateRequest(
        Long orgId,
        String location,
        boolean isActive) {

}
