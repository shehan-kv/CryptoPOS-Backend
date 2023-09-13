package com.cryptopos.inventory.service.item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cryptopos.inventory.dto.ItemCreateRequest;
import com.cryptopos.inventory.dto.ItemResult;
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

        Long pageNumLong = Long.parseLong(pageNum.orElse("1"));
        Long pageSizeLong = Long.parseLong(pageSize.orElse("20"));
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
}
