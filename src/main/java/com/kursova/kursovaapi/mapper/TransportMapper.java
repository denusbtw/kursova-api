package com.kursova.kursovaapi.mapper;

import com.kursova.kursovaapi.dto.TransportDTO;
import com.kursova.kursovaapi.entity.TransportEntity;

/**
 * Utility class for mapping between TransportEntity and TransportDTO.
 */
public final class TransportMapper {

    public static TransportDTO toDto(TransportEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("TransportEntity must not be null");
        }

        TransportDTO dto = new TransportDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    public static TransportEntity toEntity(TransportDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("TransportDTO must not be null");
        }

        TransportEntity entity = new TransportEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}
