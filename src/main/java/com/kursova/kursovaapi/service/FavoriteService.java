package com.kursova.kursovaapi.service;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.entity.FavoriteEntity;
import com.kursova.kursovaapi.entity.TourEntity;
import com.kursova.kursovaapi.mapper.TourMapper;
import com.kursova.kursovaapi.repository.FavoriteRepository;
import com.kursova.kursovaapi.repository.TourRepository;
import com.kursova.kursovaapi.repository.custom.TourSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервіс для керування улюбленими турами.
 */
@Service
public class FavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteService.class);

    private final FavoriteRepository favoriteRepository;
    private final TourRepository tourRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, TourRepository tourRepository) {
        this.favoriteRepository = favoriteRepository;
        this.tourRepository = tourRepository;
    }

    /**
     * Додає тур до обраного, якщо він ще не доданий.
     */
    public void addToFavorites(int tourId) {
        tourRepository.findById(tourId).ifPresentOrElse(
                tour -> {
                    if (!favoriteRepository.existsByTour_Id(tourId)) {
                        favoriteRepository.save(new FavoriteEntity(tour));
                        logger.info("Tour {} added to favorites", tourId);
                    } else {
                        logger.debug("Tour {} already in favorites", tourId);
                    }
                },
                () -> {
                    throw new IllegalArgumentException("Invalid tour ID: " + tourId);
                }
        );
    }

    /**
     * Видаляє всі записи з улюбленого за ID туру.
     * @Transactional — потрібна, бо це DELETE-запит.
     */
    @Transactional
    public void removeByTourId(int tourId) {
        logger.info("Removing tour {} from favorites", tourId);
        favoriteRepository.deleteByTourId(tourId);
        logger.info("Tour {} removed from favorites", tourId);
    }

    /**
     * Отримує всі улюблені тури як DTO.
     * Встановлює прапор isFavorite = true.
     */
    public List<TourDTO> getAll() {
        logger.info("Retrieving all favorite tours");
        return favoriteRepository.findAll().stream()
                .map(fav -> TourMapper.toDto(fav.getTour()))
                .peek(dto -> dto.setIsFavorite(true))
                .toList();
    }

    /**
     * Шукає тури серед обраних, використовуючи динамічні фільтри.
     */
    public Page<TourDTO> searchFavorites(
            String name, String type, String mealOption,
            Integer minDays, Integer maxDays,
            Integer minPrice, Integer maxPrice,
            Double minRating, Double maxRating,
            String transportName,
            Pageable pageable
    ) {
        // Побудова лог-повідомлення
        StringBuilder message = new StringBuilder("Searching favorites with filters:");
        if (name != null && !name.isBlank()) message.append(" name='").append(name).append("'");
        if (type != null && !type.isBlank()) message.append(", type='").append(type).append("'");
        if (transportName != null && !transportName.isBlank()) message.append(", transport='").append(transportName).append("'");
        if (mealOption != null && !mealOption.isBlank()) message.append(", meal='").append(mealOption).append("'");
        if (minDays != null || maxDays != null)
            message.append(", days=[").append(minDays != null ? minDays : "").append("-").append(maxDays != null ? maxDays : "").append("]");
        if (minPrice != null || maxPrice != null)
            message.append(", price=[").append(minPrice != null ? minPrice : "").append("-").append(maxPrice != null ? maxPrice : "").append("]");
        if (minRating != null || maxRating != null)
            message.append(", rating=[").append(minRating != null ? minRating : "").append("-").append(maxRating != null ? maxRating : "").append("]");
        logger.info(message.toString());

        // Побудова динамічного запиту через Specification
        Specification<TourEntity> spec = Specification.where(TourSpecification.nameContains(name))
                .and(TourSpecification.hasType(type))
                .and(TourSpecification.hasMealOption(mealOption))
                .and(TourSpecification.minDays(minDays))
                .and(TourSpecification.maxDays(maxDays))
                .and(TourSpecification.minPrice(minPrice))
                .and(TourSpecification.maxPrice(maxPrice))
                .and(TourSpecification.minRating(minRating))
                .and(TourSpecification.maxRating(maxRating))
                .and(TourSpecification.hasTransportName(transportName));

        // Беремо всі ID улюблених турів
        List<Integer> favoriteTourIds = favoriteRepository.findAll().stream()
                .map(fav -> fav.getTour().getId())
                .toList();

        // Шукаємо всі тури по фільтрах, а потім залишаємо лише ті, що в улюблених
        List<TourDTO> filteredFavorites = tourRepository.findAll(spec).stream()
                .filter(tour -> favoriteTourIds.contains(tour.getId()))
                .map(TourMapper::toDto)
                .peek(dto -> dto.setIsFavorite(true))
                .toList();

        int total = filteredFavorites.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<TourDTO> pageContent = start <= end ? filteredFavorites.subList(start, end) : List.of();

        logger.info("Returning page {} with {} elements out of {}", pageable.getPageNumber(), pageContent.size(), total);
        return new PageImpl<>(pageContent, pageable, total);
    }
}
