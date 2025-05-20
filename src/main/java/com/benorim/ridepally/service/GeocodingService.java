package com.benorim.ridepally.service;

import com.benorim.ridepally.dto.geocode.GeocodeRequestDTO;
import com.benorim.ridepally.dto.geocode.GeocodeResponseDTO;
import com.benorim.ridepally.entity.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final RestClient restClient;

    public GeocodingService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "ridepally/1.0") // Required
                .build();
    }

    public Location getLocation(GeocodeRequestDTO request) {
        String endpoint = String.format(
                "/reverse?format=jsonv2&lat=%f&lon=%f",
                request.getLat(), request.getLon()
        );

        return restClient.get()
                .uri(endpoint)
                .retrieve()
                .body(Location.class);
    }
}
