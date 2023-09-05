package com.cryptopos.orgs.dto;

public record OrgResult(
        Long id,
        String name,
        String currency,
        String country,
        boolean isActive) {

}
