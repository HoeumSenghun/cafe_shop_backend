package com.cafe_shop.product.service.impl;

import com.cafe_shop.common.exception.ResourceNotFoundException;
import com.cafe_shop.product.dto.ProductDtos.CreateProductRequest;
import com.cafe_shop.product.dto.ProductDtos.ProductResponse;
import com.cafe_shop.product.dto.ProductDtos.UpdateProductRequest;
import com.cafe_shop.product.mapper.ProductMapper;
import com.cafe_shop.product.model.Product;
import com.cafe_shop.product.repository.ProductRepository;
import com.cafe_shop.product.repository.ProductSpecifications;
import com.cafe_shop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest req) {
        Product p = new Product();
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setCategory(req.category());
        p.setImageUrl(req.imageUrl());
        if (req.isAvailable() != null) {
            p.setAvailable(req.isAvailable());
        }
        return productMapper.toResponse(productRepository.save(p));
    }

    @Override
    @Transactional
    public ProductResponse update(long id, UpdateProductRequest req) {
        Product p = getActiveEntity(id);
        if (req.name() != null) p.setName(req.name());
        if (req.description() != null) p.setDescription(req.description());
        if (req.price() != null) p.setPrice(req.price());
        if (req.category() != null) p.setCategory(req.category());
        if (req.imageUrl() != null) p.setImageUrl(req.imageUrl());
        if (req.isAvailable() != null) p.setAvailable(req.isAvailable());
        return productMapper.toResponse(productRepository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(long id) {
        return productMapper.toResponse(getActiveEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> list(String q, String category, Boolean isAvailable, Pageable pageable) {
        Specification<Product> spec = ProductSpecifications.notDeleted();
        if (q != null && !q.isBlank()) spec = spec.and(ProductSpecifications.search(q));
        if (category != null && !category.isBlank()) spec = spec.and(ProductSpecifications.hasCategory(category));
        if (isAvailable != null) spec = spec.and(ProductSpecifications.hasAvailability(isAvailable));
        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public void softDelete(long id) {
        Product p = getActiveEntity(id);
        p.setDeleted(true);
        p.setDeletedAt(Instant.now());
        productRepository.save(p);
    }

    private Product getActiveEntity(long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (p.isDeleted()) {
            throw new ResourceNotFoundException("Product not found");
        }
        return p;
    }
}

