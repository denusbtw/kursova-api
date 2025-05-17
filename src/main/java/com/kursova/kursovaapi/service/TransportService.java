package com.kursova.kursovaapi.service;

import com.kursova.kursovaapi.dto.TransportDTO;
import com.kursova.kursovaapi.entity.TransportEntity;
import com.kursova.kursovaapi.mapper.TransportMapper;
import com.kursova.kursovaapi.repository.TransportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportService {

    private static final Logger logger = LoggerFactory.getLogger(TransportService.class);
    private final TransportRepository repository;

    public TransportService(TransportRepository repository) {
        this.repository = repository;
    }

    public TransportDTO getById(int id) {
        logger.info("Fetching transport with ID {}", id);
        TransportEntity entity = repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Transport with ID {} not found", id);
                    return new IllegalArgumentException("Transport not found");
                });
        return TransportMapper.toDto(entity);
    }

    public List<TransportDTO> getAll() {
        logger.info("Fetching all transport entries");
        return repository.findAll().stream()
                .map(TransportMapper::toDto)
                .toList();
    }

    public TransportDTO create(TransportDTO dto) {
        logger.info("Creating new transport: {}", dto.getName());
        TransportEntity entity = TransportMapper.toEntity(dto);
        TransportEntity saved = repository.save(entity);
        logger.info("Transport created with ID {}", saved.getId());
        return TransportMapper.toDto(saved);
    }

    public void delete(int id) {
        logger.info("Deleting transport with ID {}", id);
        repository.deleteById(id);
        logger.info("Transport with ID {} deleted", id);
    }

    public TransportDTO update(int id, TransportDTO dto) {
        logger.info("Updating transport with ID {}", id);
        TransportEntity entity = repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Transport with ID {} not found for update", id);
                    return new IllegalArgumentException("Invalid transport ID");
                });
        entity.setName(dto.getName());
        TransportEntity updated = repository.save(entity);
        logger.info("Transport with ID {} updated successfully", updated.getId());
        return TransportMapper.toDto(updated);
    }
}

