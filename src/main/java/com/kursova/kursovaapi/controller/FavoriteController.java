package com.kursova.kursovaapi.controller;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.service.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public void add(@RequestBody int tourId) {
        logger.info("Received request to add tour {} to favorites", tourId);
        favoriteService.addToFavorites(tourId);
    }

    @DeleteMapping("/{tourId}")
    public void remove(@PathVariable int tourId) {
        logger.info("Received request to remove tour {} from favorites", tourId);
        favoriteService.removeByTourId(tourId);
    }

    @GetMapping
    public Page<TourDTO> getAll(
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
        logger.info("GET /api/favorites called with filters");
        return favoriteService.searchFavorites(
                name, type, mealOption,
                minDays, maxDays,
                minPrice, maxPrice,
                minRating, maxRating,
                transportName, pageable
        );
    }
}
