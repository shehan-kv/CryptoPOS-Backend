package com.cryptopos.orgs.dto;

public record OrgDetailsResult(
        Long id,
        String name,
        String currency,
        String country,
        boolean isActive,
        Long employees) {

}
