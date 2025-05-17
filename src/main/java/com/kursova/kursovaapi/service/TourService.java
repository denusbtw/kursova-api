package com.kursova.kursovaapi.service;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.entity.TourEntity;
import com.kursova.kursovaapi.entity.TransportEntity;
import com.kursova.kursovaapi.mapper.TourMapper;
import com.kursova.kursovaapi.repository.TourRepository;
import com.kursova.kursovaapi.repository.TransportRepository;
import com.kursova.kursovaapi.repository.custom.TourSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TourService {
    private static final Logger logger = LoggerFactory.getLogger(TourService.class);

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
        logger.info("Creating tour: {}", dto.getName());

        TransportEntity transport = transportRepository.findById(dto.getTransportId())
                .orElseThrow(() -> {
                    logger.error("Transport with ID {} not found", dto.getTransportId());
                    return new IllegalArgumentException("Invalid transport ID");
                });

        TourEntity entity = TourMapper.toEntity(dto, transport);
        TourEntity saved = tourRepository.save(entity);

        logger.info("Tour created successfully with ID {}", saved.getId());
        return TourMapper.toDto(saved);
    }

    public Page<TourDTO> searchToursWithPaging(String name, String type, String mealOption,
                                               Integer minDays, Integer maxDays,
                                               Integer minPrice, Integer maxPrice,
                                               Double minRating, Double maxRating,
                                               String transportName,
                                               Pageable pageable) {
        StringBuilder logMessage = new StringBuilder("Searching tours with filters:");

        if (name != null && !name.isBlank()) logMessage.append(" name='").append(name).append("'");
        if (type != null && !type.isBlank()) logMessage.append(", type='").append(type).append("'");
        if (mealOption != null && !mealOption.isBlank()) logMessage.append(", meal='").append(mealOption).append("'");
        if (transportName != null && !transportName.isBlank()) logMessage.append(", transport='").append(transportName).append("'");

        if (minDays != null || maxDays != null)
            logMessage.append(", days=[").append(minDays != null ? minDays : "").append("-").append(maxDays != null ? maxDays : "").append("]");

        if (minPrice != null || maxPrice != null)
            logMessage.append(", price=[").append(minPrice != null ? minPrice : "").append("-").append(maxPrice != null ? maxPrice : "").append("]");

        if (minRating != null || maxRating != null)
            logMessage.append(", rating=[").append(minRating != null ? minRating : "").append("-").append(maxRating != null ? maxRating : "").append("]");

        logger.info(logMessage.toString());

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

        logger.info("Querying database for filtered tours");

        return tourRepository.findAll(spec, pageable).map(entity -> {
            TourDTO dto = TourMapper.toDto(entity);
            dto.setIsFavorite(favoriteIds.contains(dto.getId()));
            return dto;
        });
    }

    public List<String> getAllTypes() {
        logger.info("Fetching all distinct tour types");
        return tourRepository.findAllDistinctTypes();
    }

    public List<String> getAllMealOptions() {
        logger.info("Fetching all distinct meal options");
        return tourRepository.findAllDistinctMealOptions();
    }
}

