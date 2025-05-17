package com.kursova.kursovaapi.service;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.entity.FavoriteEntity;
import com.kursova.kursovaapi.entity.TourEntity;
import com.kursova.kursovaapi.mapper.TourMapper;
import com.kursova.kursovaapi.repository.FavoriteRepository;
import com.kursova.kursovaapi.repository.TourRepository;
import com.kursova.kursovaapi.repository.custom.TourSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final TourRepository tourRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, TourRepository tourRepository) {
        this.favoriteRepository = favoriteRepository;
        this.tourRepository = tourRepository;
    }

    public void addToFavorites(int tourId) {
        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tour ID"));

        if (!favoriteRepository.existsByTour_Id(tourId)) {
            favoriteRepository.save(new FavoriteEntity(tour));
        }
    }

    @Transactional
    public void removeByTourId(int tourId) {
        favoriteRepository.deleteByTourId(tourId);
    }

    public List<TourDTO> getAll() {
        return favoriteRepository.findAll().stream()
                .map(fav -> TourMapper.toDto(fav.getTour()))
                .toList();
    }

    public List<TourDTO> searchFavorites(
            String name, String type, String mealOption,
            Integer minDays, Integer maxDays,
            Integer minPrice, Integer maxPrice,
            Double minRating, Double maxRating,
            String transportName
    ) {
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

        List<Integer> favoriteTourIds = favoriteRepository.findAll().stream()
                .map(f -> f.getTour().getId())
                .toList();

        return tourRepository.findAll(spec).stream()
                .filter(t -> favoriteTourIds.contains(t.getId()))
                .map(TourMapper::toDto)
                .peek(dto -> dto.setIsFavorite(true))
                .toList();
    }
}

