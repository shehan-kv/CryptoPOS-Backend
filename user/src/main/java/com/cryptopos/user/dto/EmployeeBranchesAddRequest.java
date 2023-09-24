package com.cryptopos.user.dto;

import java.util.List;

public record EmployeeBranchesAddRequest(
        Long userId,
        List<Long> branches) {

}
