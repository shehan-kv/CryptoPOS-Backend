package com.cryptopos.user.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.user.dto.UserDetailsAuthResult;
import com.cryptopos.user.entity.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByEmail(String email);

    @Query(value = """
            SELECT u.email, u.password, u.is_active, u.is_verified, r.name AS role
            FROM USERS u
            JOIN ROLES r ON r.id = u.role_id
            WHERE u.email = :email
            """)
    Mono<UserDetailsAuthResult> findAuthDetailsByEmail(String email);

}
