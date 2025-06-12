package com.kursova.kursovaapi.repository;

import com.kursova.kursovaapi.entity.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TourRepository extends JpaRepository<TourEntity, Integer>, JpaSpecificationExecutor<TourEntity> {

    @Query("SELECT DISTINCT t.type FROM TourEntity t")
    List<String> findAllDistinctTypes();

    @Query("SELECT DISTINCT t.mealOption FROM TourEntity t WHERE t.mealOption IS NOT NULL")
    List<String> findAllDistinctMealOptions();
}
