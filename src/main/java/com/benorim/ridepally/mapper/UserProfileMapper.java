package com.benorim.ridepally.mapper;

import com.benorim.ridepally.dto.profile.UserProfileResponseDTO;
import com.benorim.ridepally.entity.Location;
import com.benorim.ridepally.entity.UserProfile;

public final class UserProfileMapper {
    
    private UserProfileMapper() {
        // Private constructor to prevent instantiation
    }
    
    public static UserProfileResponseDTO toResponseDTO(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }

        return UserProfileResponseDTO.builder()
                .id(userProfile.getId())
                .userId(userProfile.getRidepallyUser().getId())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .displayName(userProfile.getDisplayName())
                .email(userProfile.getRidepallyUser().getEmail())
                .city(userProfile.getLocation().getCity())
                .state(userProfile.getLocation().getState())
                .zipCode(userProfile.getLocation().getZipCode())
                .createdAt(userProfile.getCreatedAt())
                .lastUpdate(userProfile.getLastUpdate())
                .build();
    }
}
