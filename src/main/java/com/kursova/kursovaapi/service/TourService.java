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

/**
 * Сервіс для створення та пошуку турів.
 * TourService = аналог Django views або business-логіки в DRF.
 */
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

    /**
     * Створює новий тур.
     * Перевіряє існування пов’язаного транспорту.
     * Аналог:
     *   transport = get_object_or_404(Transport, pk=dto.transport_id)
     *   Tour.objects.create(...)
     */
    public TourDTO createTour(TourDTO dto) {
        logger.info("Creating tour: {}", dto.getName());

        // Перевірка, що транспорт існує
        TransportEntity transport = transportRepository.findById(dto.getTransportId())
                .orElseThrow(() -> {
                    logger.error("Transport with ID {} not found", dto.getTransportId());
                    return new IllegalArgumentException("Invalid transport ID");
                });

        // Перетворення DTO -> Entity
        TourEntity entity = TourMapper.toEntity(dto, transport);
        TourEntity saved = tourRepository.save(entity); // INSERT

        logger.info("Tour created successfully with ID {}", saved.getId());

        // Повернення DTO для відповіді
        return TourMapper.toDto(saved);
    }

    /**
     * Шукає тури з підтримкою пагінації та фільтрації.
     * Крім того, позначає, які з них знаходяться в улюблених.
     */
    public Page<TourDTO> searchToursWithPaging(
            String name, String type, String mealOption,
            Integer minDays, Integer maxDays,
            Integer minPrice, Integer maxPrice,
            Double minRating, Double maxRating,
            String transportName,
            Pageable pageable
    ) {
        // Побудова лог-повідомлення для дебагу
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

        // Побудова динамічного фільтра (аналог Django Q-об'єктів)
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

        // Отримуємо список ID улюблених турів
        List<Integer> favoriteIds = favoriteService.getAll().stream()
                .map(TourDTO::getId)
                .toList();

        logger.info("Querying database for filtered tours");

        // Пошук турів по фільтрам із автоматичною пагінацією
        return tourRepository.findAll(spec, pageable).map(entity -> {
            TourDTO dto = TourMapper.toDto(entity);
            dto.setIsFavorite(favoriteIds.contains(dto.getId())); // Позначити, чи є в обраному
            return dto;
        });
    }

    /**
     * Повертає всі унікальні типи турів (для фільтрів, UI).
     */
    public List<String> getAllTypes() {
        logger.info("Fetching all distinct tour types");
        return tourRepository.findAllDistinctTypes();
    }

    /**
     * Повертає всі унікальні опції харчування (для фільтрів, UI).
     */
    public List<String> getAllMealOptions() {
        logger.info("Fetching all distinct meal options");
        return tourRepository.findAllDistinctMealOptions();
    }
}
