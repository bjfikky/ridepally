package com.benorim.ridepally.dto.motorcycle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MotorcycleResponseDTO {
    private Long id;
    private String make;
    private String model;
    private String year;
    private String color;
    private String nickname;
    private Long userProfileId;
} 