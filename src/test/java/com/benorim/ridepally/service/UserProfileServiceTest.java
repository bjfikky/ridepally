package com.benorim.ridepally.service;

import com.benorim.ridepally.dto.profile.CreateUserProfileRequestDTO;
import com.benorim.ridepally.dto.profile.UpdateUserProfileRequestDTO;
import com.benorim.ridepally.entity.Location;
import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.entity.UserProfile;
import com.benorim.ridepally.exception.DataOwnershipException;
import com.benorim.ridepally.repository.RidepallyUserRepository;
import com.benorim.ridepally.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private RidepallyUserRepository ridepallyUserRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserProfileService userProfileService;

    private UUID userId;
    private RidepallyUser ridepallyUser;
    private UserProfile userProfile;
    private CreateUserProfileRequestDTO createRequest;
    private UpdateUserProfileRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ridepallyUser = new RidepallyUser();
        ridepallyUser.setId(userId);

        Location location = Location.builder()
                .City("New York")
                .State("NY")
                .ZipCode("10001")
                .build();

        userProfile = UserProfile.builder()
                .id(1L)
                .ridepallyUser(ridepallyUser)
                .firstName("John")
                .lastName("Doe")
                .displayName("johndoe")
                .location(location)
                .build();

        createRequest = new CreateUserProfileRequestDTO();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDisplayName("johndoe");
        createRequest.setCity("New York");
        createRequest.setState("NY");
        createRequest.setZipCode("10001");

        updateRequest = new UpdateUserProfileRequestDTO();
        updateRequest.setFirstName("John");
        updateRequest.setLastName("Doe");
        updateRequest.setDisplayName("johndoe");
        updateRequest.setCity("New York");
        updateRequest.setState("NY");
        updateRequest.setZipCode("10001");
    }

    @Test
    void createUserProfile_Success() {
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.of(ridepallyUser));
        when(userProfileRepository.existsByDisplayNameIgnoreCase(anyString())).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        UserProfile result = userProfileService.createUserProfile(userId, createRequest);

        assertNotNull(result);
        assertEquals(userProfile.getDisplayName(), result.getDisplayName());
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void createUserProfile_UserNotFound() {
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            userProfileService.createUserProfile(userId, createRequest)
        );
    }

    @Test
    void createUserProfile_DisplayNameTaken() {
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.of(ridepallyUser));
        when(userProfileRepository.existsByDisplayNameIgnoreCase(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            userProfileService.createUserProfile(userId, createRequest)
        );
    }

    @Test
    void getUserProfile_Success() {
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.of(ridepallyUser));
        when(userProfileRepository.findByRidepallyUser(ridepallyUser)).thenReturn(Optional.of(userProfile));

        UserProfile result = userProfileService.getUserProfile(userId);

        assertNotNull(result);
        assertEquals(userProfile.getDisplayName(), result.getDisplayName());
    }

    @Test
    void getUserProfile_UserNotFound() {
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            userProfileService.getUserProfile(userId)
        );
    }

    @Test
    void getUserProfile_ProfileNotFound() {
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.of(ridepallyUser));
        when(userProfileRepository.findByRidepallyUser(ridepallyUser)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            userProfileService.getUserProfile(userId)
        );
    }

    @Test
    void updateUserProfile_Success() {
        // Set a different display name in the update request
        updateRequest.setDisplayName("newdisplayname");
        
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.of(ridepallyUser));
        when(userProfileRepository.findByRidepallyUser(ridepallyUser)).thenReturn(Optional.of(userProfile));
        when(authService.isRequestMadeByLoggedInUserOrAdmin(any())).thenReturn(true);
        when(userProfileRepository.existsByDisplayNameIgnoreCase("newdisplayname")).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        UserProfile result = userProfileService.updateUserProfile(userId, updateRequest);

        assertNotNull(result);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void updateUserProfile_Unauthorized() {
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.of(ridepallyUser));
        when(userProfileRepository.findByRidepallyUser(ridepallyUser)).thenReturn(Optional.of(userProfile));
        when(authService.isRequestMadeByLoggedInUserOrAdmin(any())).thenReturn(false);

        assertThrows(DataOwnershipException.class, () -> 
            userProfileService.updateUserProfile(userId, updateRequest)
        );
    }

    @Test
    void updateUserProfile_DisplayNameTaken() {
        // Set a different display name that's already taken
        updateRequest.setDisplayName("takenname");
        
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.of(ridepallyUser));
        when(userProfileRepository.findByRidepallyUser(ridepallyUser)).thenReturn(Optional.of(userProfile));
        when(authService.isRequestMadeByLoggedInUserOrAdmin(any())).thenReturn(true);
        when(userProfileRepository.existsByDisplayNameIgnoreCase("takenname")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            userProfileService.updateUserProfile(userId, updateRequest)
        );
    }

    @Test
    void findByDisplayName_Success() {
        String displayName = "johndoe";
        when(userProfileRepository.findByDisplayNameIgnoreCase(displayName)).thenReturn(Optional.of(userProfile));

        Optional<UserProfile> result = userProfileService.findByDisplayName(displayName);

        assertTrue(result.isPresent());
        assertEquals(displayName, result.get().getDisplayName());
    }

    @Test
    void existsByDisplayName_Success() {
        String displayName = "johndoe";
        when(userProfileRepository.existsByDisplayNameIgnoreCase(displayName)).thenReturn(true);

        boolean result = userProfileService.existsByDisplayName(displayName);

        assertTrue(result);
    }

    @Test
    void existsByUserId_Success() {
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.of(ridepallyUser));
        when(userProfileRepository.findByRidepallyUser(ridepallyUser)).thenReturn(Optional.of(userProfile));

        boolean result = userProfileService.existsByUserId(userId);

        assertTrue(result);
    }

    @Test
    void existsByUserId_UserNotFound() {
        when(ridepallyUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            userProfileService.existsByUserId(userId)
        );
    }
} 