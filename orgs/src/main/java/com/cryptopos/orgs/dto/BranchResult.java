package com.cryptopos.orgs.dto;

public record BranchResult(
        Long id,
        Long orgId,
        String location,
        boolean isActive) {

}
