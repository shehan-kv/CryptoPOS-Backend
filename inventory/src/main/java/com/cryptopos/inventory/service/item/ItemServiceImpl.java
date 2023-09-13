package com.cryptopos.inventory.service.item;

import java.time.LocalDateTime;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cryptopos.inventory.dto.ItemCreateRequest;
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

}
