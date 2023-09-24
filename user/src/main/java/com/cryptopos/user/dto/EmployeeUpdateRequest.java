package com.cryptopos.user.dto;

import java.util.List;

public record EmployeeUpdateRequest(
                String firstName,
                String lastName,
                List<Long> branches,
                boolean isActive,
                String role) {

}
