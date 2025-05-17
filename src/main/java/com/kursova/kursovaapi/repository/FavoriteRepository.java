package com.kursova.kursovaapi.repository;

import com.kursova.kursovaapi.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Integer> {
    boolean existsByTour_Id(int tourId);

    @Modifying
    @Query("DELETE FROM FavoriteEntity f WHERE f.tour.id = :tourId")
    void deleteByTourId(@Param("tourId") int tourId);

    List<FavoriteEntity> findAll();
}

