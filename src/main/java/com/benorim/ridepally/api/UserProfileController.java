package com.benorim.ridepally.api;

import com.benorim.ridepally.dto.profile.CreateUserProfileRequestDTO;
import com.benorim.ridepally.dto.profile.UpdateUserProfileRequestDTO;
import com.benorim.ridepally.dto.profile.UserProfileResponseDTO;
import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.entity.UserProfile;
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

import java.util.UUID;

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
        RidepallyUser user = userProfileService.getUserProfile(userId).getRidepallyUser();
        
        UserProfile userProfile = userProfileService.createUserProfile(
                user,
                request.getFirstName(),
                request.getLastName(),
                request.getDisplayName()
        );

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
        UserProfile userProfile = userProfileService.updateUserProfile(
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getDisplayName()
        );
        return ResponseEntity.ok(toResponseDTO(userProfile));
    }

    @GetMapping("/check-display-name/{displayName}")
    public ResponseEntity<Boolean> checkDisplayNameAvailability(@PathVariable String displayName) {
        boolean isAvailable = !userProfileService.existsByDisplayName(displayName);
        return ResponseEntity.ok(isAvailable);
    }
} 