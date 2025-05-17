package com.kursova.kursovaapi.mapper;

import com.kursova.kursovaapi.dto.TransportDTO;
import com.kursova.kursovaapi.entity.TransportEntity;

public class TransportMapper {

    public static TransportDTO toDto(TransportEntity entity) {
        TransportDTO dto = new TransportDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    public static TransportEntity toEntity(TransportDTO dto) {
        TransportEntity entity = new TransportEntity();
        entity.setName(dto.getName());
        return entity;
    }
}
