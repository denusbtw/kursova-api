package com.kursova.kursovaapi.controller;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.service.TourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контролер для турів.
 * Відповідає за:
 * - Пошук і фільтрацію турів з пагінацією.
 * - Створення нового туру.
 * - Отримання доступних типів і meal-опцій.
 */
@RestController
@RequestMapping("/api/tours")
public class TourController {

    private static final Logger logger = LoggerFactory.getLogger(TourController.class);

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    /**
     * Пошук турів із фільтрами та підтримкою пагінації.
     * Аналог: `GET /api/tours?type=пляж&minDays=5&page=0&size=10`
     */
    @GetMapping
    public Page<TourDTO> getTours(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String mealOption,
            @RequestParam(required = false) Integer minDays,
            @RequestParam(required = false) Integer maxDays,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) String transportName,
            Pageable pageable
    ) {
        logger.info("GET /api/tours called with filters");
        return tourService.searchToursWithPaging(
                name, type, mealOption,
                minDays, maxDays,
                minPrice, maxPrice,
                minRating, maxRating,
                transportName, pageable
        );
    }

    /**
     * Створює новий тур.
     * Очікує JSON-обʼєкт із даними туру.
     * Аналог: `POST /api/tours` з тілою `{"name": "...", "type": "...", ...}`
     */
    @PostMapping
    public TourDTO createTour(@RequestBody TourDTO dto) {
        logger.info("POST /api/tours called to create tour: {}", dto.getName());
        return tourService.createTour(dto);
    }

    /**
     * Повертає список усіх унікальних типів турів.
     * Для UI-фільтрів.
     */
    @GetMapping("/types")
    public List<String> getTourTypes() {
        logger.info("GET /api/tours/types called");
        return tourService.getAllTypes();
    }

    /**
     * Повертає список унікальних meal-опцій, які використовуються в турах.
     * Для UI-фільтрів.
     */
    @GetMapping("/mealOptions")
    public List<String> getMealOptions() {
        logger.info("GET /api/tours/mealOptions called");
        return tourService.getAllMealOptions();
    }
}
