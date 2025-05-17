package com.kursova.kursovaapi.mapper;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.entity.TourEntity;
import com.kursova.kursovaapi.entity.TransportEntity;

public class TourMapper {

    public static TourDTO toDto(TourEntity entity) {
        TourDTO dto = new TourDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setMealOption(entity.getMealOption());
        dto.setNumberOfDays(entity.getNumberOfDays());
        dto.setPrice(entity.getPrice());
        dto.setRating(entity.getRating());
        dto.setTransportId(entity.getTransport() != null ? entity.getTransport().getId() : 0);

        TransportEntity transport = entity.getTransport();
        if (transport != null) {
            dto.setTransportId(transport.getId());
            dto.setTransportName(transport.getName());
        }

        return dto;
    }

    public static TourEntity toEntity(TourDTO dto, TransportEntity transport) {
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