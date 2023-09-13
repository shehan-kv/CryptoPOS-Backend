package com.cryptopos.inventory.service.item;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cryptopos.inventory.dto.ItemCreateRequest;
import com.cryptopos.inventory.dto.ItemResult;
import com.cryptopos.inventory.dto.Page;

import reactor.core.publisher.Mono;

@Service
public interface ItemService {

    Mono<Boolean> createItem(Long branchId, ItemCreateRequest createRequest);

    Mono<Page<ItemResult>> getItemsByBranch(Long branchId, Optional<String> pageNum, Optional<String> pageSize);
}
