package com.cryptopos.inventory.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.inventory.dto.ItemCreateRequest;
import com.cryptopos.inventory.dto.ItemUpdateRequest;
import com.cryptopos.inventory.exceptions.NotPermittedException;
import com.cryptopos.inventory.service.item.ItemService;

import reactor.core.publisher.Mono;

@Component
public class ItemHandler {

    private final ItemService itemService;

    public ItemHandler(ItemService itemService) {
        this.itemService = itemService;
    }

    public Mono<ServerResponse> createItem(ServerRequest request) {
        return request
                .bodyToMono(ItemCreateRequest.class)
                .flatMap(createRequest -> itemService.createItem(Long.parseLong(request.pathVariable("branchId")),
                        createRequest))
                .flatMap(result -> ServerResponse.status(HttpStatus.CREATED).build())
                .onErrorResume(error -> {
                    if (error instanceof NotPermittedException) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                    }

                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    public Mono<ServerResponse> getItemsByBranch(ServerRequest request) {
        return itemService
                .getItemsByBranch(
                        Long.parseLong(request.pathVariable("branchId")),
                        request.queryParam("page"),
                        request.queryParam("size"))

                .flatMap(result -> ServerResponse.ok().bodyValue(result))
                .onErrorResume(error -> {
                    if (error instanceof NotPermittedException) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                    }

                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    public Mono<ServerResponse> updateItem(ServerRequest request) {
        return request
                .bodyToMono(ItemUpdateRequest.class)
                .flatMap(updateRequest -> itemService.updateItem(
                        Long.parseLong(request.pathVariable("itemId")),
                        updateRequest))
                .flatMap(result -> ServerResponse.ok().build())
                .onErrorResume(error -> {
                    if (error instanceof NotPermittedException) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                    }

                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}
