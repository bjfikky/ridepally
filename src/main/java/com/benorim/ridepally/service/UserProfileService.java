package com.benorim.ridepally.service;

import com.benorim.ridepally.dto.geocode.GeocodeRequestDTO;
import com.benorim.ridepally.dto.profile.CreateUserProfileRequestDTO;
import com.benorim.ridepally.dto.profile.UpdateUserProfileRequestDTO;
import com.benorim.ridepally.entity.Location;
import com.benorim.ridepally.entity.UserProfile;
import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.enums.ErrorCode;
import com.benorim.ridepally.exception.DataOwnershipException;
import com.benorim.ridepally.exception.RidepallyException;
import com.benorim.ridepally.repository.UserProfileRepository;
import com.benorim.ridepally.repository.RidepallyUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final RidepallyUserRepository ridepallyUserRepository;
    private final AuthService authService;
    private final GeocodingService geocodingService;

    @Transactional
    public UserProfile createUserProfile(UUID userId, CreateUserProfileRequestDTO request) {
        RidepallyUser ridepallyUser = ridepallyUserRepository.findById(userId)
                .orElseThrow(() -> new RidepallyException(ErrorCode.USER_NOT_FOUND ,"User not found with id: " + userId));

        if (userProfileRepository.existsByDisplayNameIgnoreCase(request.getDisplayName())) {
            throw new RidepallyException(ErrorCode.DISPLAY_NAME_TAKEN ,"Display name is already taken");
        }

        Location location = Location.builder()
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .build();

        UserProfile userProfile = UserProfile.builder()
                .ridepallyUser(ridepallyUser)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .displayName(request.getDisplayName())
                .location(location)
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
                userProfileRepository.existsByDisplayNameIgnoreCase(request.getDisplayName())) {
            throw new IllegalArgumentException("Display name is already taken");
        }

        Location location = Location.builder()
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .build();

        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setDisplayName(request.getDisplayName());
        userProfile.setLocation(location);

        return userProfileRepository.save(userProfile);
    }

    @Transactional(readOnly = true)
    public Optional<UserProfile> findByDisplayName(String displayName) {
        return userProfileRepository.findByDisplayNameIgnoreCase(displayName);
    }

    @Transactional(readOnly = true)
    public boolean existsByDisplayName(String displayName) {
        return userProfileRepository.existsByDisplayNameIgnoreCase(displayName);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        RidepallyUser ridepallyUser = ridepallyUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return userProfileRepository.findByRidepallyUser(ridepallyUser).isPresent();
    }

    @Transactional(readOnly = true)
    public List<UserProfile> findRidersByLocation(String city, String state) {
        return userProfileRepository.findByLocationCityIgnoreCaseAndLocationStateIgnoreCase(city, state);
    }

    @Transactional(readOnly = true)
    public List<UserProfile> findRidersByCoordinates(Double lat, Double lon) {
        Location location = geocodingService.getLocation(new GeocodeRequestDTO(lat, lon));

        return userProfileRepository.findByLocationCityIgnoreCaseAndLocationStateIgnoreCase(
            location.getCity(), 
            location.getState()
        );
    }
} 