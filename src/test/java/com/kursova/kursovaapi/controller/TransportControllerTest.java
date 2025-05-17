package com.kursova.kursovaapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kursova.kursovaapi.dto.TransportDTO;
import com.kursova.kursovaapi.service.TransportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // інтеграція лише веб-рівня
import org.springframework.boot.test.mock.mockito.MockBean; // мок сервісів (не репозиторіїв)
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // HTTP запити
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;   // HTTP статуси + JSON

@WebMvcTest(TransportController.class) // піднімає Spring MVC context лише з цим контролером
class TransportControllerTest {

    @Autowired
    private MockMvc mockMvc; // аналог Django APIClient: дозволяє емулювати HTTP-запити

    @MockBean
    private TransportService service; // мокає залежність контролера

    @Autowired
    private ObjectMapper objectMapper; // для серіалізації/десеріалізації JSON

    private TransportDTO dto;

    @BeforeEach
    void setup() {
        dto = new TransportDTO(); // підготовка базового DTO
        dto.setId(1);
        dto.setName("Train");
    }

    // --- GET /api/transports ---

    @Test
    void getAll_returnsListOfTransports() throws Exception {
        when(service.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/transports")) // GET запит до контролера
                .andExpect(status().isOk()) // очікується 200 OK
                .andExpect(jsonPath("$[0].id").value(1)) // перевірка JSON
                .andExpect(jsonPath("$[0].name").value("Train"));

        verify(service).getAll(); // переконання, що сервіс викликано
    }

    // --- GET /api/transports/{id} ---

    @Test
    void getById_returnsTransportDTO() throws Exception {
        when(service.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/transports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Train"));

        verify(service).getById(1);
    }

    // --- POST /api/transports ---

    @Test
    void create_validTransport_returnsCreatedDTO() throws Exception {
        when(service.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/transports")
                        .contentType("application/json") // тип тіла — JSON
                        .content(objectMapper.writeValueAsString(dto))) // сериалізуємо DTO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Train"));

        verify(service).create(any()); // будь-який вхідний DTO
    }

    // --- DELETE /api/transports/{id} ---

    @Test
    void delete_existingId_returnsOk() throws Exception {
        mockMvc.perform(delete("/api/transports/1"))
                .andExpect(status().isOk());

        verify(service).delete(1);
    }

    // --- PUT /api/transports/{id} ---

    @Test
    void update_validId_returnsUpdatedDTO() throws Exception {
        when(service.update(eq(1), any())).thenReturn(dto);

        mockMvc.perform(put("/api/transports/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Train"));

        verify(service).update(eq(1), any());
    }
}
