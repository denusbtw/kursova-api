package com.kursova.kursovaapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TourController.class) // Ізольований веб-тест тільки контролера (без full context)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc; // дає можливість емулювати HTTP-запити як APIClient в DRF

    @MockBean
    private TourService tourService; // мок сервісного шару, який інжектиться в контролер

    @Autowired
    private ObjectMapper objectMapper; // Jackson обʼєкт для серіалізації DTO у JSON

    private TourDTO tourDTO;

    @BeforeEach
    void setup() {
        // базовий обʼєкт, що буде повертатися зі всіх методів сервісу
        tourDTO = new TourDTO();
        tourDTO.setId(1);
        tourDTO.setName("Test Tour");
    }

    // --- GET /api/tours ---

    @Test
    void getTours_returnsFilteredPage() throws Exception {
        // створення mock сторінки з одним туром
        Page<TourDTO> page = new PageImpl<>(List.of(tourDTO), PageRequest.of(0, 10), 1);

        // підготовка мока
        when(tourService.searchToursWithPaging(
                any(), any(), any(),
                any(), any(), any(), any(),
                any(), any(), any(),
                any(Pageable.class)
        )).thenReturn(page);

        // виклик GET /api/tours з фільтром "name"
        mockMvc.perform(get("/api/tours")
                        .param("name", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk()) // очікуємо 200
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Tour"));

        // перевірка правильності виклику сервісу
        verify(tourService).searchToursWithPaging(
                eq("Test"), eq(null), eq(null),
                eq(null), eq(null), eq(null), eq(null),
                eq(null), eq(null), eq(null),
                any(Pageable.class)
        );
    }

    // --- POST /api/tours ---

    @Test
    void createTour_returnsCreatedTour() throws Exception {
        when(tourService.createTour(any())).thenReturn(tourDTO);

        mockMvc.perform(post("/api/tours")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tourDTO))) // перетворення обʼєкта на JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Tour"));

        verify(tourService).createTour(any());
    }

    // --- GET /api/tours/types ---

    @Test
    void getTourTypes_returnsList() throws Exception {
        List<String> types = List.of("Beach", "Mountain");
        when(tourService.getAllTypes()).thenReturn(types);

        mockMvc.perform(get("/api/tours/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Beach"))
                .andExpect(jsonPath("$[1]").value("Mountain"));

        verify(tourService).getAllTypes();
    }

    // --- GET /api/tours/mealOptions ---

    @Test
    void getMealOptions_returnsList() throws Exception {
        List<String> meals = List.of("Breakfast", "Full Board");
        when(tourService.getAllMealOptions()).thenReturn(meals);

        mockMvc.perform(get("/api/tours/mealOptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Breakfast"))
                .andExpect(jsonPath("$[1]").value("Full Board"));

        verify(tourService).getAllMealOptions();
    }
}

