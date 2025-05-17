package com.kursova.kursovaapi.repository.custom;

import com.kursova.kursovaapi.entity.TourEntity;
import org.springframework.data.jpa.domain.Specification;

public class TourSpecification {

    public static Specification<TourEntity> nameContains(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<TourEntity> hasType(String type) {
        return (root, query, cb) -> type == null ? null : cb.equal(cb.lower(root.get("type")), type.toLowerCase());
    }

    public static Specification<TourEntity> hasMealOption(String mealOption) {
        return (root, query, cb) -> mealOption == null ? null : cb.equal(cb.lower(root.get("mealOption")), mealOption.toLowerCase());
    }

    public static Specification<TourEntity> minDays(Integer minDays) {
        return (root, query, cb) -> minDays == null ? null : cb.greaterThanOrEqualTo(root.get("numberOfDays"), minDays);
    }

    public static Specification<TourEntity> maxDays(Integer maxDays) {
        return (root, query, cb) -> maxDays == null ? null : cb.lessThanOrEqualTo(root.get("numberOfDays"), maxDays);
    }

    public static Specification<TourEntity> minPrice(Integer minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<TourEntity> maxPrice(Integer maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<TourEntity> minRating(Double minRating) {
        return (root, query, cb) -> minRating == null ? null : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<TourEntity> maxRating(Double maxRating) {
        return (root, query, cb) -> maxRating == null ? null : cb.lessThanOrEqualTo(root.get("rating"), maxRating);
    }

    public static Specification<TourEntity> hasTransportName(String transportName) {
        return (root, query, cb) -> {
            if (transportName == null || transportName.isBlank()) return null;
            return cb.equal(root.join("transport").get("name"), transportName);
        };
    }
}
