package com.kursova.kursovaapi.repository;

import com.kursova.kursovaapi.entity.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Репозиторій для доступу до турів (TourEntity).
 * Наслідує:
 * - JpaRepository — CRUD, пагінація, сортування.
 * - JpaSpecificationExecutor — підтримка динамічних фільтрів через Specification API.
 */
public interface TourRepository extends JpaRepository<TourEntity, Integer>, JpaSpecificationExecutor<TourEntity> {

    /**
     * Отримує унікальні типи турів.
     *
     * JPQL: SELECT DISTINCT t.type FROM TourEntity t
     * Аналог Django ORM: Tour.objects.values_list("type", flat=True).distinct()
     */
    @Query("SELECT DISTINCT t.type FROM TourEntity t")
    List<String> findAllDistinctTypes();

    /**
     * Отримує унікальні опції харчування, які не є NULL.
     *
     * JPQL: SELECT DISTINCT t.mealOption FROM TourEntity t WHERE t.mealOption IS NOT NULL
     * Аналог Django ORM: Tour.objects.exclude(meal_option=None).values_list("meal_option", flat=True).distinct()
     */
    @Query("SELECT DISTINCT t.mealOption FROM TourEntity t WHERE t.mealOption IS NOT NULL")
    List<String> findAllDistinctMealOptions();
}
