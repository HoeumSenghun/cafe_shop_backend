package com.cafe_shop.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Standard API response wrapper")
@Builder
public record ApiEnvelope<T>(
        boolean success,
        String message,
        T data
) {
    public static <T> ApiEnvelope<T> ok(String message, T data) {
        return ApiEnvelope.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiEnvelope<T> fail(String message, T data) {
        return ApiEnvelope.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
}
