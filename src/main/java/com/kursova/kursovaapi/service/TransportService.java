package com.kursova.kursovaapi.service;

import com.kursova.kursovaapi.dto.TransportDTO;
import com.kursova.kursovaapi.entity.TransportEntity;
import com.kursova.kursovaapi.mapper.TransportMapper;
import com.kursova.kursovaapi.repository.TransportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportService {

    private final TransportRepository repository;

    public TransportService(TransportRepository repository) {
        this.repository = repository;
    }

    public TransportDTO getById(int id) {
        TransportEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transport not found"));
        return TransportMapper.toDto(entity);
    }

    public List<TransportDTO> getAll() {
        return repository.findAll().stream()
                .map(TransportMapper::toDto)
                .toList();
    }

    public TransportDTO create(TransportDTO dto) {
        TransportEntity entity = TransportMapper.toEntity(dto);
        return TransportMapper.toDto(repository.save(entity));
    }

    public void delete(int id) {
        repository.deleteById(id);
    }

    public TransportDTO update(int id, TransportDTO dto) {
        TransportEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transport ID"));
        entity.setName(dto.getName());
        return TransportMapper.toDto(repository.save(entity));
    }
}

