package com.cryptopos.inventory.service.item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cryptopos.inventory.dto.ItemCreateRequest;
import com.cryptopos.inventory.dto.ItemResult;
import com.cryptopos.inventory.dto.ItemStockUpdateRequest;
import com.cryptopos.inventory.dto.ItemStockUpdateType;
import com.cryptopos.inventory.dto.ItemUpdateRequest;
import com.cryptopos.inventory.dto.Page;
import com.cryptopos.inventory.entity.DiscountType;
import com.cryptopos.inventory.entity.Item;
import com.cryptopos.inventory.entity.TaxType;
import com.cryptopos.inventory.exceptions.NotPermittedException;
import com.cryptopos.inventory.repository.DiscountTypeRepository;
import com.cryptopos.inventory.repository.ItemRepository;
import com.cryptopos.inventory.repository.TaxTypeRepository;
import com.cryptopos.inventory.service.amqp.AmqpService;

import reactor.core.publisher.Mono;

@Component
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final TaxTypeRepository taxRepository;
    private final DiscountTypeRepository discountRepository;

    private AmqpService amqpService;

    public ItemServiceImpl(
            ItemRepository itemRepo,
            TaxTypeRepository taxRepository,
            DiscountTypeRepository discountRepository,
            AmqpService amqpService) {

        this.itemRepository = itemRepo;
        this.taxRepository = taxRepository;
        this.amqpService = amqpService;
        this.discountRepository = discountRepository;

    }

    @Override
    public Mono<Boolean> createItem(Long branchId, ItemCreateRequest createRequest) {

        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> {
                    return amqpService.getUserBranches(Long.parseLong(userId));
                })
                .map(branchList -> {
                    if (!branchList.contains(branchId)) {
                        throw new NotPermittedException();
                    }

                    return branchList;
                })
                .zipWith(taxRepository.findByType(createRequest.taxType().toUpperCase()))
                .zipWith(discountRepository.findByType(createRequest.discountType().toUpperCase()))
                .flatMap(tuple -> {
                    TaxType taxType = tuple.getT1().getT2();
                    DiscountType discountType = tuple.getT2();

                    LocalDateTime currentDateTime = LocalDateTime.now();

                    Item newItem = new Item(
                            null,
                            createRequest.lookupCode(),
                            createRequest.description(),
                            createRequest.price(),
                            createRequest.tax(),
                            taxType.id(),
                            createRequest.discount(),
                            discountType.id(),
                            createRequest.inStock(),
                            branchId,
                            currentDateTime,
                            currentDateTime);

                    return itemRepository.save(newItem).map(savedEntity -> true);
                });
    }

    @Override
    public Mono<Page<ItemResult>> getItemsByBranch(
            Long branchId,
            Optional<String> pageNum,
            Optional<String> pageSize) {

        Long pageNumLong = Math.max(Long.parseLong(pageNum.orElse("1")), 1);
        Long pageSizeLong = Math.max(Long.parseLong(pageSize.orElse("20")), 1);
        Long offset = (pageNumLong - 1) * pageSizeLong;

        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> {
                    return amqpService.getUserBranches(Long.parseLong(userId));
                })
                .map(branchList -> {
                    if (!branchList.contains(branchId)) {
                        throw new NotPermittedException();
                    }

                    return branchList;
                })
                .flatMap(branchList -> itemRepository.findItemsByBranch(branchId, offset, pageSizeLong).collectList())
                .zipWith(itemRepository.countItemsByBranch(branchId))
                .map(tuple -> {

                    List<ItemResult> itemResults = tuple.getT1();
                    Long pageCount = (long) Math.max((int) (Math.ceil(tuple.getT2()) / pageSizeLong), 1);

                    return new Page<ItemResult>(pageNumLong, pageSizeLong, pageCount, itemResults);

                });
    }

    @Override
    public Mono<Boolean> updateItem(Long itemId, ItemUpdateRequest updateRequest) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> {
                    return amqpService.getUserBranches(Long.parseLong(userId));
                })
                .zipWith(itemRepository.findById(itemId))
                .flatMap(tuple -> {
                    List<Long> brandList = tuple.getT1();
                    Item existingItem = tuple.getT2();

                    if (!brandList.contains(existingItem.branchId())) {
                        return Mono.error(new NotPermittedException());
                    }

                    return taxRepository.findByType(updateRequest.taxType().toUpperCase());
                })
                .zipWith(discountRepository.findByType(updateRequest.discountType().toUpperCase()))
                .flatMap(tuple -> {

                    TaxType taxType = tuple.getT1();
                    DiscountType discountType = tuple.getT2();

                    return itemRepository.updateItem(
                            itemId,
                            updateRequest.lookupCode(),
                            updateRequest.description(),
                            updateRequest.price(),
                            updateRequest.tax(),
                            taxType.id(),
                            updateRequest.discount(),
                            discountType.id())
                            .map(result -> true);

                });
    }

    @Override
    public Mono<Boolean> updateItemStock(Long itemId, ItemStockUpdateRequest updateRequest) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> {
                    return amqpService.getUserBranches(Long.parseLong(userId));
                })
                .zipWith(itemRepository.findById(itemId))
                .flatMap(tuple -> {
                    List<Long> brandList = tuple.getT1();
                    Item existingItem = tuple.getT2();

                    if (!brandList.contains(existingItem.branchId())) {
                        return Mono.error(new NotPermittedException());
                    }

                    if (updateRequest.type() == ItemStockUpdateType.INCREASE) {
                        return itemRepository.incrementStock(itemId, updateRequest.inStock()).map(result -> true);
                    }

                    if (updateRequest.type() == ItemStockUpdateType.DECREASE) {
                        return itemRepository.decreaseStock(itemId, updateRequest.inStock()).map(result -> true);
                    }

                    return itemRepository.clearStock(itemId).map(result -> true);

                });
    }

    @Override
    public Mono<List<ItemResult>> getItemsByBranchAndLookupCode(Long branchId, String lookupCode) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> {
                    return amqpService.getUserBranches(Long.parseLong(userId));
                })
                .map(branchList -> {
                    if (!branchList.contains(branchId)) {
                        throw new NotPermittedException();
                    }

                    return branchList;
                })
                .flatMap(branchList -> itemRepository
                        .findItemByBranchAndLookupCode(branchId, lookupCode)
                        .collectList());
    }
}
