package com.kursova.kursovaapi.repository;

import com.kursova.kursovaapi.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Репозиторій для доступу до таблиці обраних турів (FavoriteEntity).
 * Наслідує JpaRepository — надає CRUD і пагінацію.
 */
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Integer> {

    /**
     * Перевіряє, чи існує запис з певним tourId.
     *
     * Аналог Django: Favorite.objects.filter(tour__id=tour_id).exists()
     */
    boolean existsByTour_Id(int tourId);

    /**
     * Видаляє всі записи, пов’язані з певним туром.
     *
     * Аналог Django: Favorite.objects.filter(tour__id=tour_id).delete()
     *
     * @Modifying — обов’язковий для DML-запитів (DELETE, UPDATE).
     */
    @Modifying
    @Query("DELETE FROM FavoriteEntity f WHERE f.tour.id = :tourId")
    void deleteByTourId(@Param("tourId") int tourId);

//    /**
//     * Повертає всі записи. Насправді цей метод не обов'язковий —
//     * він уже є в JpaRepository як findAll(), але можливо, ти хочеш
//     * явну декларацію для автодокументації чи розширення в майбутньому.
//     */
//    @Override
//    List<FavoriteEntity> findAll();
}
