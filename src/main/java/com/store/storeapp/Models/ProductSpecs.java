package com.store.storeapp.Models;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {

    public static Specification<Product> queryLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank())
                return cb.conjunction();
            String like = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }


    public static Specification<Product> inCategory(Long categoryId) {
        return (root, query, cb) ->
                (categoryId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("category").get("id"), categoryId);
    }


    public static Specification<Product> priceBetween(Double min, Double max) {
        return (root, query, cb) ->{
            if(min == null && max == null)
                return cb.conjunction();
            if(min != null && max != null)
                return cb.between(root.get("price"), min, max);
            if(min != null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }


}
