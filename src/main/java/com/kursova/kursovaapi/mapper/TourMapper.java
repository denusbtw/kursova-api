package com.kursova.kursovaapi.mapper;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.entity.TourEntity;
import com.kursova.kursovaapi.entity.TransportEntity;

/**
 * Utility class for mapping between TourEntity and TourDTO.
 */
public final class TourMapper {

    public static TourDTO toDto(TourEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("TourEntity must not be null");
        }
        TourDTO dto = new TourDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setMealOption(entity.getMealOption());
        dto.setNumberOfDays(entity.getNumberOfDays());
        dto.setPrice(entity.getPrice());
        dto.setRating(entity.getRating());

        TransportEntity transport = entity.getTransport();
        if (transport != null) {
            dto.setTransportId(transport.getId());
            dto.setTransportName(transport.getName());
        } else {
            dto.setTransportId(0);
        }

        return dto;
    }

    public static TourEntity toEntity(TourDTO dto, TransportEntity transport) {
        if (dto == null) {
            throw new IllegalArgumentException("TourDTO must not be null");
        }

        TourEntity entity = new TourEntity();
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setMealOption(dto.getMealOption());
        entity.setNumberOfDays(dto.getNumberOfDays());
        entity.setPrice(dto.getPrice());
        entity.setRating(dto.getRating());
        entity.setTransport(transport);
        return entity;
    }
}
