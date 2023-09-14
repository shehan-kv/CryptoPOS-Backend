package com.cryptopos.inventory.repository;

import java.math.BigDecimal;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.inventory.dto.ItemResult;
import com.cryptopos.inventory.entity.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {

    @Query("""
            SELECT i.id, i.lookup_code, i.description, i.price, i.tax,
            i.discount, i.in_stock,
            t.type AS tax_type, d.type AS discount_type
            FROM items i
            JOIN tax_types t ON t.id = i.tax_type
            JOIN discount_types d ON d.id = i.discount_type
            WHERE i.branch_id = :branchId
            OFFSET :offset
            LIMIT :pageSize
                    """)
    Flux<ItemResult> findItemsByBranch(Long branchId, Long offset, Long pageSize);

    @Query("""
            SELECT COUNT(id)
            FROM items
            WHERE branch_id = :branchId
            """)
    Mono<Long> countItemsByBranch(Long branchId);

    @Modifying
    @Query("""
            UPDATE items
            SET lookup_code = :lookupCode, description = :description, price = :price, tax = :tax,
            tax_type = :taxType, discount = :discount, discount_type = :discountType
            WHERE id = :id
                """)
    Mono<Long> updateItem(
            @Param("id") Long id,
            @Param("lookupCode") String lookupCode,
            @Param("description") String description,
            @Param("price") BigDecimal price,
            @Param("tax") BigDecimal tax,
            @Param("taxType") Long taxType,
            @Param("discount") BigDecimal discount,
            @Param("discountType") Long discountType);
}
