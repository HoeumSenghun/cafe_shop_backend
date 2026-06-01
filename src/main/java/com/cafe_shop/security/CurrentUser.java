package com.cafe_shop.security;

import com.cafe_shop.user.model.RoleName;

import java.util.Set;

public record CurrentUser(
        Long userId,
        String email,
        Set<RoleName> roles
) {
}
