package com.cryptopos.orgs.dto;

import java.util.List;

public record UserBranchResponse(
        String orgName,
        List<BranchResponse> branches) {

}
