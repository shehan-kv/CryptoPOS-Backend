package com.cryptopos.orgs.dto;

import java.util.List;

public record EmployeeBranchesAddRequest(
        Long userId,
        List<Long> branches) {

}
