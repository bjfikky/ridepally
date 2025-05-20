package com.benorim.ridepally.dto.geocode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodeRequestDTO {
    private Double lat;
    private Double lon;
}
