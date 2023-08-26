package com.cryptopos.user.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.user.entity.Role;

public interface RoleRepository extends ReactiveCrudRepository<Role, UUID> {

}
