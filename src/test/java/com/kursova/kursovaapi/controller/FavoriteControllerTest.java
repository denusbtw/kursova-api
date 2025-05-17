package com.kursova.kursovaapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.service.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoriteController.class) // Тестує тільки FavoriteController, ізольовано від решти
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc; // емуляція HTTP-запитів (як DRF APIClient)

    @MockBean
    private FavoriteService favoriteService; // мок залежності контролера

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper(); // Jackson JSON-серіалізатор (вручну)
    }

    // --- POST /api/favorites ---

    @Test
    void add_toFavorites_returns200() throws Exception {
        int tourId = 5;

        mockMvc.perform(post("/api/favorites")
                        .contentType("application/json")
                        .content(String.valueOf(tourId))) // тіло запиту — простий int як JSON
                .andExpect(status().isOk()); // очікуємо 200 OK

        verify(favoriteService).addToFavorites(tourId); // перевірка виклику сервісу
    }

    // --- DELETE /api/favorites/{tourId} ---

    @Test
    void remove_fromFavorites_returns200() throws Exception {
        int tourId = 7;

        mockMvc.perform(delete("/api/favorites/{tourId}", tourId)) // DELETE з path param
                .andExpect(status().isOk());

        verify(favoriteService).removeByTourId(tourId); // переконання у виклику логіки
    }

    // --- GET /api/favorites ---

    @Test
    void getAll_returnsPageOfFavorites() throws Exception {
        // підготовка мокованої відповіді з одним туром
        TourDTO dto = new TourDTO();
        dto.setId(1);
        dto.setName("Test Tour");

        Page<TourDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        // коли викликається сервіс, повертається сторінка з 1 DTO
        when(favoriteService.searchFavorites(
                any(), any(), any(),
                any(), any(), any(), any(),
                any(), any(), any(),
                any(Pageable.class)
        )).thenReturn(page);

        // GET-запит з query-параметрами
        mockMvc.perform(get("/api/favorites")
                        .param("name", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk()) // має бути 200
                .andExpect(jsonPath("$.content[0].id").value(1)) // перевірка JSON-відповіді
                .andExpect(jsonPath("$.content[0].name").value("Test Tour"));

        // перевірка, що метод викликано з тими самими параметрами
        verify(favoriteService).searchFavorites(
                eq("Test"), eq(null), eq(null),
                eq(null), eq(null), eq(null), eq(null),
                eq(null), eq(null), eq(null),
                any(Pageable.class)
        );
    }
}

