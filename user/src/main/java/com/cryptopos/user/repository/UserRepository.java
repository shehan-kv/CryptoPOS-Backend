package com.cryptopos.user.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.user.entity.User;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {

}