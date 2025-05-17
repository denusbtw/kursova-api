package com.kursova.kursovaapi.service;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.entity.TourEntity;
import com.kursova.kursovaapi.entity.TransportEntity;
import com.kursova.kursovaapi.mapper.TourMapper;
import com.kursova.kursovaapi.repository.TourRepository;
import com.kursova.kursovaapi.repository.TransportRepository;
import com.kursova.kursovaapi.repository.custom.TourSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TourService {

    private final TourRepository tourRepository;
    private final TransportRepository transportRepository;
    private final FavoriteService favoriteService;

    public TourService(
            TourRepository tourRepository,
            TransportRepository transportRepository,
            FavoriteService favoriteService
    ) {
        this.tourRepository = tourRepository;
        this.transportRepository = transportRepository;
        this.favoriteService = favoriteService;
    }

    public TourDTO createTour(TourDTO dto) {
        TransportEntity transport = transportRepository.findById(dto.getTransportId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid transport ID"));

        TourEntity entity = TourMapper.toEntity(dto, transport);
        TourEntity saved = tourRepository.save(entity);
        return TourMapper.toDto(saved);
    }

    public Page<TourDTO> searchToursWithPaging(String name, String type, String mealOption,
                                               Integer minDays, Integer maxDays,
                                               Integer minPrice, Integer maxPrice,
                                               Double minRating, Double maxRating,
                                               String transportName,
                                               Pageable pageable) {

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

        List<Integer> favoriteIds = favoriteService.getAll().stream()
                .map(TourDTO::getId)
                .toList();

        return tourRepository.findAll(spec, pageable).map(entity -> {
            TourDTO dto = TourMapper.toDto(entity);
            dto.setIsFavorite(favoriteIds.contains(dto.getId()));
            return dto;
        });
    }

    public List<String> getAllTypes() {
        return tourRepository.findAllDistinctTypes();
    }

    public List<String> getAllMealOptions() {
        return tourRepository.findAllDistinctMealOptions();
    }
}

