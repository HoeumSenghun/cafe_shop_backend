package com.cafe_shop.product.controller;

import com.cafe_shop.common.api.ApiEnvelope;
import com.cafe_shop.product.dto.ProductDtos.CreateProductRequest;
import com.cafe_shop.product.dto.ProductDtos.ProductResponse;
import com.cafe_shop.product.dto.ProductDtos.UpdateProductRequest;
import com.cafe_shop.product.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @SecurityRequirements
    @GetMapping
    public ResponseEntity<ApiEnvelope<Page<ProductResponse>>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        String[] parts = sort.split(",", 2);
        String sortBy = parts[0];
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(ApiEnvelope.ok("OK", productService.list(q, category, isAvailable, pageable)));
    }

    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<ApiEnvelope<ProductResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", productService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiEnvelope<ProductResponse>> create(@Valid @RequestBody CreateProductRequest req) {
        return ResponseEntity.ok(ApiEnvelope.ok("Created", productService.create(req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiEnvelope<ProductResponse>> update(@PathVariable long id, @Valid @RequestBody UpdateProductRequest req) {
        return ResponseEntity.ok(ApiEnvelope.ok("Updated", productService.update(id, req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiEnvelope<Object>> delete(@PathVariable long id) {
        productService.softDelete(id);
        return ResponseEntity.ok(ApiEnvelope.ok("Deleted", null));
    }
}

