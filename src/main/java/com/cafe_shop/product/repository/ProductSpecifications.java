package com.cafe_shop.product.repository;

import com.cafe_shop.product.model.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {
    private ProductSpecifications() {}

    public static Specification<Product> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    public static Specification<Product> hasAvailability(Boolean isAvailable) {
        return (root, query, cb) -> cb.equal(root.get("isAvailable"), isAvailable);
    }

    public static Specification<Product> search(String q) {
        return (root, query, cb) -> {
            String like = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }
}

