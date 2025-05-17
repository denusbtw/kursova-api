package com.kursova.kursovaapi.controller;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.service.TourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tours")
public class TourController {

    private static final Logger logger = LoggerFactory.getLogger(TourController.class);
    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

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
                name, type, mealOption, minDays, maxDays,
                minPrice, maxPrice, minRating, maxRating,
                transportName, pageable
        );
    }

    @PostMapping
    public TourDTO createTour(@RequestBody TourDTO dto) {
        logger.info("POST /api/tours called to create tour: {}", dto.getName());
        return tourService.createTour(dto);
    }

    @GetMapping("/types")
    public List<String> getTourTypes() {
        logger.info("GET /api/tours/types called");
        return tourService.getAllTypes();
    }

    @GetMapping("/mealOptions")
    public List<String> getMealOptions() {
        logger.info("GET /api/tours/mealOptions called");
        return tourService.getAllMealOptions();
    }

}
