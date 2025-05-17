package com.kursova.kursovaapi.controller;

import com.kursova.kursovaapi.dto.TransportDTO;
import com.kursova.kursovaapi.service.TransportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transports")
public class TransportController {

    private final TransportService service;

    public TransportController(TransportService service) {
        this.service = service;
    }

    @GetMapping
    public List<TransportDTO> getAll() {
        return service.getAll();
    }

    @PostMapping
    public TransportDTO create(@RequestBody TransportDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public TransportDTO getById(@PathVariable int id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @PutMapping("/{id}")
    public TransportDTO update(@PathVariable int id, @RequestBody TransportDTO dto) {
        return service.update(id, dto);
    }
}

