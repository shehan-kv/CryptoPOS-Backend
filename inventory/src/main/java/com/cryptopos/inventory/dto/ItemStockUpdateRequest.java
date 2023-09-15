package com.cryptopos.inventory.dto;

public record ItemStockUpdateRequest(
        ItemStockUpdateType type,
        Long inStock) {

}
