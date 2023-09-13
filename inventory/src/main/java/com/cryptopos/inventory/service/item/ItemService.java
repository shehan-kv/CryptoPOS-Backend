package com.cryptopos.inventory.service.item;

import org.springframework.stereotype.Service;

import com.cryptopos.inventory.dto.ItemCreateRequest;

import reactor.core.publisher.Mono;

@Service
public interface ItemService {

    Mono<Boolean> createItem(Long branchId, ItemCreateRequest createRequest);
}
