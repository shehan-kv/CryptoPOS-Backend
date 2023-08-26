package com.cryptopos.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_role")
public record UserRole(@Id UserRolePrimaryKey id) {

}
