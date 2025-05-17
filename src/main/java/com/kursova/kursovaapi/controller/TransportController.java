package com.kursova.kursovaapi.controller;

import com.kursova.kursovaapi.dto.TransportDTO;
import com.kursova.kursovaapi.service.TransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transports")
public class TransportController {

    private static final Logger logger = LoggerFactory.getLogger(TransportController.class);
    private final TransportService service;

    public TransportController(TransportService service) {
        this.service = service;
    }

    @GetMapping
    public List<TransportDTO> getAll() {
        logger.info("GET /api/transports called");
        return service.getAll();
    }

    @PostMapping
    public TransportDTO create(@RequestBody TransportDTO dto) {
        logger.info("POST /api/transports called to create transport: {}", dto.getName());
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public TransportDTO getById(@PathVariable int id) {
        logger.info("GET /api/transports/{} called", id);
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        logger.info("DELETE /api/transports/{} called", id);
        service.delete(id);
    }

    @PutMapping("/{id}")
    public TransportDTO update(@PathVariable int id, @RequestBody TransportDTO dto) {
        logger.info("PUT /api/transports/{} called with new name: {}", id, dto.getName());
        return service.update(id, dto);
    }
}

