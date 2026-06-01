package com.cafe_shop.product.mapper;

import com.cafe_shop.product.dto.ProductDtos.ProductResponse;
import com.cafe_shop.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .category(p.getCategory())
                .imageUrl(p.getImageUrl())
                .isAvailable(p.isAvailable())
                .createdAt(p.getCreatedAt())
                .build();
    }
}

