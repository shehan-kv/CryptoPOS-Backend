package com.cryptopos.orgs.dto;

public record CreateOrgRequest(
        String name,
        String country,
        String currency,
        String branchLocation) {
}
