package com.cafe_shop.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class AuthDtos {

    @Builder
    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 2, max = 120) String fullName,
            @NotBlank @Size(min = 8, max = 72) String password
    ) {}

    @Builder
    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    @Builder
    public record RefreshRequest(
            @NotBlank String refreshToken
    ) {}

    @Builder
    public record AuthResponse(
            String accessToken,
            String refreshToken,
            long expiresInSeconds
    ) {}
}

