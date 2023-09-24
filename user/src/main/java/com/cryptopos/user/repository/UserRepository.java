package com.cryptopos.user.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cryptopos.user.dto.UserDetailsAuthResult;
import com.cryptopos.user.entity.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByEmail(String email);

    @Query("""
            SELECT u.id, u.password, u.is_active, u.is_verified, r.name AS role
            FROM USERS u
            JOIN ROLES r ON r.id = u.role_id
            WHERE u.email = :email
            """)
    Mono<UserDetailsAuthResult> findAuthDetailsByEmail(String email);

    @Modifying
    @Query("""
            UPDATE users SET first_name = :firstName, last_name = :lastName,
            is_active = :isActive, role_id = :roleId
            WHERE id = :employeeId
            """)
    Mono<Long> updateEmployee(
            @Param("employeeId") Long employeeId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("isActive") boolean isActive,
            @Param("roleId") Long roleId);

}
