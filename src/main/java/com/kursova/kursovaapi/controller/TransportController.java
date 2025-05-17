package com.kursova.kursovaapi.controller;

import com.kursova.kursovaapi.dto.TransportDTO;
import com.kursova.kursovaapi.service.TransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контролер для транспорту.
 * Обробляє CRUD-запити:
 * - GET /api/transports
 * - GET /api/transports/{id}
 * - POST /api/transports
 * - PUT /api/transports/{id}
 * - DELETE /api/transports/{id}
 */
@RestController
@RequestMapping("/api/transports")
public class TransportController {

    private static final Logger logger = LoggerFactory.getLogger(TransportController.class);

    private final TransportService service;

    public TransportController(TransportService service) {
        this.service = service;
    }

    /**
     * Отримати список усіх транспортів.
     * Аналог: GET /api/transports
     */
    @GetMapping
    public List<TransportDTO> getAll() {
        logger.info("GET /api/transports called");
        return service.getAll();
    }

    /**
     * Створити новий транспорт.
     * Очікує JSON: { "name": "..." }
     * Аналог: POST /api/transports
     */
    @PostMapping
    public TransportDTO create(@RequestBody TransportDTO dto) {
        logger.info("POST /api/transports called to create transport: {}", dto.getName());
        return service.create(dto);
    }

    /**
     * Отримати транспорт за ID.
     * Аналог: GET /api/transports/5
     */
    @GetMapping("/{id}")
    public TransportDTO getById(@PathVariable int id) {
        logger.info("GET /api/transports/{} called", id);
        return service.getById(id);
    }

    /**
     * Видалити транспорт за ID.
     * Аналог: DELETE /api/transports/5
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        logger.info("DELETE /api/transports/{} called", id);
        service.delete(id);
    }

    /**
     * Оновити транспорт за ID.
     * Очікує JSON: { "name": "..." }
     * Аналог: PUT /api/transports/5
     */
    @PutMapping("/{id}")
    public TransportDTO update(@PathVariable int id, @RequestBody TransportDTO dto) {
        logger.info("PUT /api/transports/{} called with new name: {}", id, dto.getName());
        return service.update(id, dto);
    }
}
