package com.benorim.ridepally.mapper;

import com.benorim.ridepally.dto.motorcycle.MotorcycleResponseDTO;
import com.benorim.ridepally.entity.Motorcycle;

public final class MotorcycleMapper {
    
    private MotorcycleMapper() {
        // Private constructor to prevent instantiation
    }
    
    public static MotorcycleResponseDTO toResponseDTO(Motorcycle motorcycle) {
        if (motorcycle == null) {
            return null;
        }

        return MotorcycleResponseDTO.builder()
                .id(motorcycle.getId())
                .make(motorcycle.getMake())
                .model(motorcycle.getModel())
                .year(motorcycle.getYear())
                .color(motorcycle.getColor())
                .nickname(motorcycle.getNickname())
                .userProfileId(motorcycle.getUserProfile().getId())
                .build();
    }
} 