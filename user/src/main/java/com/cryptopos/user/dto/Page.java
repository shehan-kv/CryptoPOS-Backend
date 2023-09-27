package com.cryptopos.user.dto;

import java.util.Collection;

public record Page<T>(Long currentPage, Long pageSize, Long totalPages, Collection<T> data) {

}
