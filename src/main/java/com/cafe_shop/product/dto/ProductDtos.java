package com.cafe_shop.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductDtos {

    @Builder
    public record ProductResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            String category,
            String imageUrl,
            boolean isAvailable,
            Instant createdAt
    ) {}

    @Builder
    public record CreateProductRequest(
            @NotBlank @Size(max = 160) String name,
            @Size(max = 2000) String description,
            @NotNull @DecimalMin("0.00") BigDecimal price,
            @NotBlank @Size(max = 80) String category,
            @Size(max = 500) String imageUrl,
            Boolean isAvailable
    ) {}

    @Builder
    public record UpdateProductRequest(
            @Size(max = 160) String name,
            @Size(max = 2000) String description,
            @DecimalMin("0.00") BigDecimal price,
            @Size(max = 80) String category,
            @Size(max = 500) String imageUrl,
            Boolean isAvailable
    ) {}
}

