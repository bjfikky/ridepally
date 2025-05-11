package com.benorim.ridepally.service;

import com.benorim.ridepally.dto.profile.CreateUserProfileRequestDTO;
import com.benorim.ridepally.dto.profile.UpdateUserProfileRequestDTO;
import com.benorim.ridepally.entity.UserProfile;
import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.exception.DataOwnershipException;
import com.benorim.ridepally.repository.UserProfileRepository;
import com.benorim.ridepally.repository.RidepallyUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final RidepallyUserRepository ridepallyUserRepository;
    private final AuthService authService;

    @Transactional
    public UserProfile createUserProfile(UUID userId, CreateUserProfileRequestDTO request) {
        RidepallyUser ridepallyUser = ridepallyUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (userProfileRepository.existsByDisplayName(request.getDisplayName())) {
            throw new IllegalArgumentException("Display name is already taken");
        }

        UserProfile userProfile = UserProfile.builder()
                .ridepallyUser(ridepallyUser)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .displayName(request.getDisplayName())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .build();

        return userProfileRepository.save(userProfile);
    }

    @Transactional(readOnly = true)
    public UserProfile getUserProfile(UUID userId) {
        RidepallyUser ridepallyUser = ridepallyUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return userProfileRepository.findByRidepallyUser(ridepallyUser)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found for user: " + userId));
    }

    @Transactional
    public UserProfile updateUserProfile(UUID userId, UpdateUserProfileRequestDTO request) {
        UserProfile userProfile = getUserProfile(userId);
        
        // Check if the user is authorized to update this profile
        if (!authService.isRequestMadeByLoggedInUserOrAdmin(userProfile.getRidepallyUser())) {
            throw new DataOwnershipException("You are not authorized to update this profile");
        }

        // Check if display name is being changed and if it's already taken
        if (!userProfile.getDisplayName().equals(request.getDisplayName()) && 
                userProfileRepository.existsByDisplayName(request.getDisplayName())) {
            throw new IllegalArgumentException("Display name is already taken");
        }

        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setDisplayName(request.getDisplayName());
        userProfile.setCity(request.getCity());
        userProfile.setState(request.getState());
        userProfile.setZipCode(request.getZipCode());

        return userProfileRepository.save(userProfile);
    }

    @Transactional(readOnly = true)
    public Optional<UserProfile> findByDisplayName(String displayName) {
        return userProfileRepository.findByDisplayName(displayName);
    }

    @Transactional(readOnly = true)
    public boolean existsByDisplayName(String displayName) {
        return userProfileRepository.existsByDisplayName(displayName);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        RidepallyUser ridepallyUser = ridepallyUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return userProfileRepository.findByRidepallyUser(ridepallyUser).isPresent();
    }
} 