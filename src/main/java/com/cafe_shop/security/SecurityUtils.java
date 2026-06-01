package com.cafe_shop.security;

import com.cafe_shop.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() {}

    public static CurrentUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CurrentUser cu)) {
            throw new UnauthorizedException("Unauthenticated");
        }
        return cu;
    }
}

