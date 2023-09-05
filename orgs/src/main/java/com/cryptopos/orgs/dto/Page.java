package com.cryptopos.orgs.dto;

import java.util.Collection;

public record Page<T>(int currentPage, int pageSize, int totalPages, Collection<T> data) {

}
