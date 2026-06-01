package com.cafe_shop.product.service;

import com.cafe_shop.product.dto.ProductDtos.CreateProductRequest;
import com.cafe_shop.product.dto.ProductDtos.ProductResponse;
import com.cafe_shop.product.dto.ProductDtos.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse create(CreateProductRequest req);
    ProductResponse update(long id, UpdateProductRequest req);
    ProductResponse getById(long id);
    Page<ProductResponse> list(String q, String category, Boolean isAvailable, Pageable pageable);
    void softDelete(long id);
}

