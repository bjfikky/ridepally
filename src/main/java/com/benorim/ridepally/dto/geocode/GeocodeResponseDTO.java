package com.benorim.ridepally.dto.geocode;

import com.benorim.ridepally.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodeResponseDTO {
    private Location location;
}
