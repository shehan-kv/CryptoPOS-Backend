package com.cryptopos.user.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.user.entity.Role;

import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveCrudRepository<Role, Long> {

    Mono<Role> findByName(String name);
}
