package com.benorim.ridepally.controller;

import com.benorim.ridepally.dto.profile.CreateUserProfileRequestDTO;
import com.benorim.ridepally.dto.profile.FindRidersByCoordinatesRequestDTO;
import com.benorim.ridepally.dto.profile.FindRidersRequestDTO;
import com.benorim.ridepally.dto.profile.UpdateUserProfileRequestDTO;
import com.benorim.ridepally.dto.profile.UserProfileResponseDTO;
import com.benorim.ridepally.entity.UserProfile;
import com.benorim.ridepally.mapper.UserProfileMapper;
import com.benorim.ridepally.service.AuthService;
import com.benorim.ridepally.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.benorim.ridepally.mapper.UserProfileMapper.toResponseDTO;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> createUserProfile(
            @Valid @RequestBody CreateUserProfileRequestDTO request) {
        UUID userId = authService.getSignedInUserId();
        
        // Check if user already has a profile
        if (userProfileService.existsByUserId(userId)) {
            log.info("User profile already exists for {}", userId);
            return ResponseEntity.badRequest().build();
        }
        
        UserProfile userProfile = userProfileService.createUserProfile(userId, request);
        return new ResponseEntity<>(toResponseDTO(userProfile), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable UUID userId) {
        UserProfile userProfile = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(toResponseDTO(userProfile));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getCurrentUserProfile() {
        UUID userId = authService.getSignedInUserId();
        UserProfile userProfile = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(toResponseDTO(userProfile));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserProfileRequestDTO request) {
        UserProfile userProfile = userProfileService.updateUserProfile(userId, request);
        return ResponseEntity.ok(toResponseDTO(userProfile));
    }

    @GetMapping("/check-display-name/{displayName}")
    public ResponseEntity<Boolean> checkDisplayNameAvailability(@PathVariable String displayName) {
        boolean isAvailable = !userProfileService.existsByDisplayName(displayName);
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/find-riders")
    public ResponseEntity<List<UserProfileResponseDTO>> findRidersByLocation(
            @Valid @RequestBody FindRidersRequestDTO request) {
        List<UserProfile> riders = userProfileService.findRidersByLocation(request.getCity(), request.getState());
        List<UserProfileResponseDTO> response = riders.stream()
                .map(UserProfileMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/find-riders-by-coordinates")
    public ResponseEntity<List<UserProfileResponseDTO>> findRidersByCoordinates(
            @Valid @RequestBody FindRidersByCoordinatesRequestDTO request) {
        List<UserProfile> riders = userProfileService.findRidersByCoordinates(request.getLat(), request.getLon());
        List<UserProfileResponseDTO> response = riders.stream()
                .map(UserProfileMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
} 