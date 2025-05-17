package com.kursova.kursovaapi.repository.custom;

import com.kursova.kursovaapi.entity.TourEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Цей клас містить набір фільтрів (специфікацій), які можна комбінувати
 * для побудови динамічних запитів до бази даних.
 *
 * Specification<T> — це інтерфейс із Spring Data JPA для створення SQL WHERE умов.
 */
public final class TourSpecification {

    // Приватний конструктор забороняє створення об'єктів цього класу
    private TourSpecification() {
    }

    /**
     * Повертає умову: назва туру містить заданий текст (регістр не враховується).
     */
    public static Specification<TourEntity> nameContains(String name) {
        return (root, query, cb) -> {
            // Якщо параметр порожній або null, фільтр не застосовується (null)
            if (!StringUtils.hasText(name)) {
                return null;
            }
            // SQL: LOWER(name) LIKE %<name>%
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Повертає умову: тип туру точно дорівнює заданому (регістр не враховується).
     */
    public static Specification<TourEntity> hasType(String type) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(type)) {
                return null;
            }
            return cb.equal(cb.lower(root.get("type")), type.toLowerCase());
        };
    }

    /**
     * Повертає умову: опція харчування (mealOption) дорівнює заданій.
     */
    public static Specification<TourEntity> hasMealOption(String mealOption) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(mealOption)) {
                return null;
            }
            return cb.equal(cb.lower(root.get("mealOption")), mealOption.toLowerCase());
        };
    }

    /**
     * Повертає умову: кількість днів туру ≥ minDays.
     */
    public static Specification<TourEntity> minDays(Integer minDays) {
        return (root, query, cb) -> minDays == null ? null :
                cb.greaterThanOrEqualTo(root.get("numberOfDays"), minDays);
    }

    /**
     * Повертає умову: кількість днів туру ≤ maxDays.
     */
    public static Specification<TourEntity> maxDays(Integer maxDays) {
        return (root, query, cb) -> maxDays == null ? null :
                cb.lessThanOrEqualTo(root.get("numberOfDays"), maxDays);
    }

    /**
     * Повертає умову: ціна туру ≥ minPrice.
     */
    public static Specification<TourEntity> minPrice(Integer minPrice) {
        return (root, query, cb) -> minPrice == null ? null :
                cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    /**
     * Повертає умову: ціна туру ≤ maxPrice.
     */
    public static Specification<TourEntity> maxPrice(Integer maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null :
                cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    /**
     * Повертає умову: рейтинг туру ≥ minRating.
     */
    public static Specification<TourEntity> minRating(Double minRating) {
        return (root, query, cb) -> minRating == null ? null :
                cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    /**
     * Повертає умову: рейтинг туру ≤ maxRating.
     */
    public static Specification<TourEntity> maxRating(Double maxRating) {
        return (root, query, cb) -> maxRating == null ? null :
                cb.lessThanOrEqualTo(root.get("rating"), maxRating);
    }

    /**
     * Повертає умову: назва пов'язаного транспорту (transport.name) дорівнює заданій.
     * Тут використовується JOIN між Tour і Transport.
     */
    public static Specification<TourEntity> hasTransportName(String transportName) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(transportName)) {
                return null;
            }
            // JOIN transport ON transport.id = tour.transport_id
            return cb.equal(cb.lower(root.join("transport").get("name")), transportName.toLowerCase());
        };
    }
}
