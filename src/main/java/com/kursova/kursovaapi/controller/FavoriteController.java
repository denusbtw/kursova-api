package com.kursova.kursovaapi.controller;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public void add(@RequestBody int tourId) {
        favoriteService.addToFavorites(tourId);
    }

    @DeleteMapping("/{tourId}")
    public void remove(@PathVariable int tourId) {
        favoriteService.removeByTourId(tourId);
    }

    @GetMapping
    public List<TourDTO> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String mealOption,
            @RequestParam(required = false) Integer minDays,
            @RequestParam(required = false) Integer maxDays,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) String transportName
    ) {
        return favoriteService.searchFavorites(
                name, type, mealOption,
                minDays, maxDays, minPrice, maxPrice, minRating, maxRating, transportName
        );
    }
}

