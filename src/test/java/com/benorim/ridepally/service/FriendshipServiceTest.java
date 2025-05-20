package com.benorim.ridepally.service;

import com.benorim.ridepally.dto.friendship.FriendshipActionDTO;
import com.benorim.ridepally.entity.Friendship;
import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.entity.UserProfile;
import com.benorim.ridepally.enums.FriendshipStatus;
import com.benorim.ridepally.exception.DataOwnershipException;
import com.benorim.ridepally.repository.FriendshipRepository;
import com.benorim.ridepally.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private FriendshipService friendshipService;

    private UUID currentUserId;
    private UUID otherUserId;
    private UserProfile currentUserProfile;
    private UserProfile otherUserProfile;
    private Friendship friendship;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        RidepallyUser currentUser = new RidepallyUser();
        currentUser.setId(currentUserId);
        currentUser.setEmail("current@example.com");

        RidepallyUser otherUser = new RidepallyUser();
        otherUser.setId(otherUserId);
        otherUser.setEmail("other@example.com");

        currentUserProfile = UserProfile.builder()
                .id(1L)
                .ridepallyUser(currentUser)
                .displayName("currentUser")
                .build();

        otherUserProfile = UserProfile.builder()
                .id(2L)
                .ridepallyUser(otherUser)
                .displayName("otherUser")
                .build();

        friendship = Friendship.builder()
                .id(1L)
                .requester(currentUserProfile)
                .receiver(otherUserProfile)
                .status(FriendshipStatus.PENDING)
                .build();
    }

    @Test
    void sendFriendRequest_Success() {
        when(authService.getSignedInUserId()).thenReturn(currentUserId);
        when(userProfileRepository.findByRidepallyUserId(currentUserId)).thenReturn(Optional.of(currentUserProfile));
        when(userProfileRepository.findByRidepallyUserId(otherUserId)).thenReturn(Optional.of(otherUserProfile));
        when(friendshipRepository.existsByRequesterAndReceiver(currentUserProfile, otherUserProfile)).thenReturn(false);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship response = friendshipService.sendFriendRequest(otherUserId);

        assertNotNull(response);
        assertEquals(currentUserProfile, response.getRequester());
        assertEquals(otherUserProfile, response.getReceiver());
        assertEquals(FriendshipStatus.PENDING, response.getStatus());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void sendFriendRequest_UserNotFound() {
        when(authService.getSignedInUserId()).thenReturn(currentUserId);
        when(userProfileRepository.findByRidepallyUserId(currentUserId)).thenReturn(Optional.of(currentUserProfile));
        when(userProfileRepository.findByRidepallyUserId(otherUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            friendshipService.sendFriendRequest(otherUserId)
        );
    }

    @Test
    void sendFriendRequest_RequestAlreadyExists() {
        when(authService.getSignedInUserId()).thenReturn(currentUserId);
        when(userProfileRepository.findByRidepallyUserId(currentUserId)).thenReturn(Optional.of(currentUserProfile));
        when(userProfileRepository.findByRidepallyUserId(otherUserId)).thenReturn(Optional.of(otherUserProfile));
        when(friendshipRepository.existsByRequesterAndReceiver(currentUserProfile, otherUserProfile)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> 
            friendshipService.sendFriendRequest(otherUserId)
        );
    }

    @Test
    void respondToFriendRequest_Success() {
        FriendshipActionDTO action = new FriendshipActionDTO();
        action.setAction(FriendshipStatus.ACCEPTED);

        when(authService.getSignedInUserId()).thenReturn(otherUserId);
        when(userProfileRepository.findByRidepallyUserId(otherUserId)).thenReturn(Optional.of(otherUserProfile));
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship response = friendshipService.respondToFriendRequest(1L, action);

        assertNotNull(response);
        assertEquals(FriendshipStatus.ACCEPTED, response.getStatus());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void respondToFriendRequest_NotAuthorized() {
        FriendshipActionDTO action = new FriendshipActionDTO();
        action.setAction(FriendshipStatus.ACCEPTED);

        when(authService.getSignedInUserId()).thenReturn(currentUserId);
        when(userProfileRepository.findByRidepallyUserId(currentUserId)).thenReturn(Optional.of(currentUserProfile));
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));

        assertThrows(DataOwnershipException.class, () -> 
            friendshipService.respondToFriendRequest(1L, action)
        );
    }

    @Test
    void respondToFriendRequest_NotPending() {
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        FriendshipActionDTO action = new FriendshipActionDTO();
        action.setAction(FriendshipStatus.ACCEPTED);

        when(authService.getSignedInUserId()).thenReturn(otherUserId);
        when(userProfileRepository.findByRidepallyUserId(otherUserId)).thenReturn(Optional.of(otherUserProfile));
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));

        assertThrows(IllegalStateException.class, () -> 
            friendshipService.respondToFriendRequest(1L, action)
        );
    }

    @Test
    void getPendingFriendRequests_Success() {
        List<Friendship> pendingRequests = new ArrayList<>();
        pendingRequests.add(friendship);

        when(authService.getSignedInUserId()).thenReturn(otherUserId);
        when(userProfileRepository.findByRidepallyUserId(otherUserId)).thenReturn(Optional.of(otherUserProfile));
        when(friendshipRepository.findByReceiverAndStatus(otherUserProfile, FriendshipStatus.PENDING))
                .thenReturn(pendingRequests);

        List<Friendship> response = friendshipService.getPendingFriendRequests();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(FriendshipStatus.PENDING, response.getFirst().getStatus());
    }

    @Test
    void getFriends_Success() {
        List<Friendship> friendships = new ArrayList<>();
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendships.add(friendship);

        when(authService.getSignedInUserId()).thenReturn(currentUserId);
        when(userProfileRepository.findByRidepallyUserId(currentUserId)).thenReturn(Optional.of(currentUserProfile));
        when(friendshipRepository.findByRequesterAndStatus(currentUserProfile, FriendshipStatus.ACCEPTED))
                .thenReturn(friendships);
        when(friendshipRepository.findByReceiverAndStatus(currentUserProfile, FriendshipStatus.ACCEPTED))
                .thenReturn(new ArrayList<>());

        List<Friendship> response = friendshipService.getFriends();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(FriendshipStatus.ACCEPTED, response.getFirst().getStatus());
    }
} 