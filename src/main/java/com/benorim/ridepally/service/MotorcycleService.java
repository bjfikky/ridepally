package com.benorim.ridepally.service;

import com.benorim.ridepally.dto.motorcycle.CreateMotorcycleRequestDTO;
import com.benorim.ridepally.entity.Motorcycle;
import com.benorim.ridepally.entity.UserProfile;
import com.benorim.ridepally.exception.DataOwnershipException;
import com.benorim.ridepally.repository.MotorcycleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MotorcycleService {

    private final MotorcycleRepository motorcycleRepository;
    private final UserProfileService userProfileService;
    private final AuthService authService;

    @Transactional
    public Motorcycle addMotorcycle(UUID userId, CreateMotorcycleRequestDTO request) {
        UserProfile userProfile = userProfileService.getUserProfile(userId);
        
        // Check if the user is authorized to add a motorcycle
        if (!authService.isRequestMadeByLoggedInUserOrAdmin(userProfile.getRidepallyUser())) {
            throw new DataOwnershipException("You are not authorized to add a motorcycle to this profile");
        }

        // Check if the user already used the same nickname for one of their motorcycles
        if (StringUtils.isNotBlank(request.getNickname()) &&
                motorcycleRepository.existsByUserProfileAndNickname(userProfile, request.getNickname())) {
            throw new IllegalArgumentException("Nickname is already taken for this user");
        }

        Motorcycle motorcycle = Motorcycle.builder()
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .color(request.getColor())
                .nickname(request.getNickname())
                .userProfile(userProfile)
                .build();

        return motorcycleRepository.save(motorcycle);
    }

    @Transactional
    public List<Motorcycle> addMotorcycles(UUID userId, List<CreateMotorcycleRequestDTO> requests) {
        UserProfile userProfile = userProfileService.getUserProfile(userId);
        
        // Check if the user is authorized to add motorcycles
        if (!authService.isRequestMadeByLoggedInUserOrAdmin(userProfile.getRidepallyUser())) {
            throw new DataOwnershipException("You are not authorized to add motorcycles to this profile");
        }

        // Validate nicknames
        requests.forEach(request -> {
            if (StringUtils.isNotBlank(request.getNickname()) &&
                    motorcycleRepository.existsByUserProfileAndNickname(userProfile, request.getNickname())) {
                throw new IllegalArgumentException("Nickname '" + request.getNickname() + "' is already taken for this user");
            }
        });

        // Create and save all motorcycles
        List<Motorcycle> motorcycles = requests.stream()
                .map(request -> Motorcycle.builder()
                        .make(request.getMake())
                        .model(request.getModel())
                        .year(request.getYear())
                        .color(request.getColor())
                        .nickname(request.getNickname())
                        .userProfile(userProfile)
                        .build())
                .toList();

        return motorcycleRepository.saveAll(motorcycles);
    }

    @Transactional(readOnly = true)
    public List<Motorcycle> getUserMotorcycles(UUID userId) {
        UserProfile userProfile = userProfileService.getUserProfile(userId);
        return motorcycleRepository.findByUserProfile(userProfile);
    }

    @Transactional(readOnly = true)
    public Motorcycle getMotorcycle(Long motorcycleId) {
        Motorcycle motorcycle = motorcycleRepository.findById(motorcycleId)
                .orElseThrow(() -> new IllegalArgumentException("Motorcycle not found with id: " + motorcycleId));

        // Check if the user is authorized to view this motorcycle
        if (!authService.isRequestMadeByLoggedInUserOrAdmin(motorcycle.getUserProfile().getRidepallyUser())) {
            throw new DataOwnershipException("You are not authorized to view this motorcycle");
        }

        return motorcycle;
    }

    @Transactional
    public void deleteMotorcycle(Long motorcycleId) {
        Motorcycle motorcycle = motorcycleRepository.findById(motorcycleId)
                .orElseThrow(() -> new IllegalArgumentException("Motorcycle not found with id: " + motorcycleId));

        // Check if the user is authorized to delete this motorcycle
        if (!authService.isRequestMadeByLoggedInUserOrAdmin(motorcycle.getUserProfile().getRidepallyUser())) {
            throw new DataOwnershipException("You are not authorized to delete this motorcycle");
        }

        motorcycleRepository.delete(motorcycle);
    }
} 