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

@Service
public class FavoriteService {
    private static final Logger logger = LoggerFactory.getLogger(FavoriteService.class);

    private final FavoriteRepository favoriteRepository;
    private final TourRepository tourRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, TourRepository tourRepository) {
        this.favoriteRepository = favoriteRepository;
        this.tourRepository = tourRepository;
    }

    public void addToFavorites(int tourId) {
        logger.info("Attempting to add tour {} to favorites", tourId);

        TourEntity tour;
        try {
            tour = tourRepository.findById(tourId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid tour ID"));
        } catch (IllegalArgumentException e) {
            logger.warn("Tour with ID {} not found", tourId);
            throw e;
        }

        if (!favoriteRepository.existsByTour_Id(tourId)) {
            favoriteRepository.save(new FavoriteEntity(tour));
            logger.info("Tour {} added to favorites", tourId);
        } else {
            logger.info("Tour {} already in favorites", tourId);
        }
    }

    @Transactional
    public void removeByTourId(int tourId) {
        logger.info("Removing tour {} from favorites", tourId);
        favoriteRepository.deleteByTourId(tourId);
        logger.info("Tour {} removed from favorites", tourId);
    }

    public List<TourDTO> getAll() {
        logger.info("Retrieving all favorite tours");
        return favoriteRepository.findAll().stream()
                .map(fav -> TourMapper.toDto(fav.getTour()))
                .peek(dto -> dto.setIsFavorite(true))
                .toList();
    }

    public Page<TourDTO> searchFavorites(
            String name, String type, String mealOption,
            Integer minDays, Integer maxDays,
            Integer minPrice, Integer maxPrice,
            Double minRating, Double maxRating,
            String transportName,
            Pageable pageable
    ) {
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

        // Беремо всі улюблені ID
        List<Integer> favoriteTourIds = favoriteRepository.findAll().stream()
                .map(f -> f.getTour().getId())
                .toList();

        // Фільтруємо всі тури за Specification та перехресно з улюбленими
        List<TourDTO> filteredFavorites = tourRepository.findAll(spec).stream()
                .filter(t -> favoriteTourIds.contains(t.getId()))
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

