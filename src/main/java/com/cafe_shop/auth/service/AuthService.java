package com.cafe_shop.auth.service;

import com.cafe_shop.auth.dto.AuthDtos.AuthResponse;
import com.cafe_shop.auth.dto.AuthDtos.LoginRequest;
import com.cafe_shop.auth.dto.AuthDtos.RefreshRequest;
import com.cafe_shop.auth.dto.AuthDtos.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
    AuthResponse refresh(RefreshRequest req);
    void logout(RefreshRequest req);
}

