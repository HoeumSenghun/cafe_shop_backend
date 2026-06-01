package com.cafe_shop.auth.controller;

import com.cafe_shop.auth.dto.AuthDtos.AuthResponse;
import com.cafe_shop.auth.dto.AuthDtos.LoginRequest;
import com.cafe_shop.auth.dto.AuthDtos.RefreshRequest;
import com.cafe_shop.auth.dto.AuthDtos.RegisterRequest;
import com.cafe_shop.auth.service.AuthService;
import com.cafe_shop.common.api.ApiEnvelope;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<ApiEnvelope<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(ApiEnvelope.ok("Registered", authService.register(req)));
    }

    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<ApiEnvelope<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiEnvelope.ok("Logged in", authService.login(req)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiEnvelope<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest req) {
        return ResponseEntity.ok(ApiEnvelope.ok("Refreshed", authService.refresh(req)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiEnvelope<Object>> logout(@Valid @RequestBody RefreshRequest req) {
        authService.logout(req);
        return ResponseEntity.ok(ApiEnvelope.ok("Logged out", null));
    }
}

