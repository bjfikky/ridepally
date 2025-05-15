package com.benorim.ridepally.service;

import com.benorim.ridepally.dto.FriendshipActionDTO;
import com.benorim.ridepally.entity.Friendship;
import com.benorim.ridepally.entity.UserProfile;
import com.benorim.ridepally.enums.FriendshipStatus;
import com.benorim.ridepally.exception.DataOwnershipException;
import com.benorim.ridepally.repository.FriendshipRepository;
import com.benorim.ridepally.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;

    @Transactional
    public Friendship sendFriendRequest(UUID receiverId) {
        UserProfile currentUser = getCurrentUserProfile();
        UserProfile receiver = userProfileRepository.findByRidepallyUserId(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (friendshipRepository.existsByRequesterAndReceiver(currentUser, receiver)) {
            throw new IllegalStateException("Friendship request already exists");
        }

        Friendship friendship = Friendship.builder()
                .requester(currentUser)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .build();

        return friendshipRepository.save(friendship);
    }

    @Transactional
    public Friendship respondToFriendRequest(Long friendshipId, FriendshipActionDTO action) {
        UserProfile currentUser = getCurrentUserProfile();
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Friendship request not found"));

        if (!friendship.getReceiver().equals(currentUser)) {
            throw new DataOwnershipException("Not authorized to respond to this request");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Friendship request is not pending");
        }

        friendship.setStatus(action.getAction());
        friendship.setUpdatedAt(LocalDateTime.now());

        return friendshipRepository.save(friendship);
    }

    @Transactional(readOnly = true)
    public List<Friendship> getPendingFriendRequests() {
        UserProfile currentUser = getCurrentUserProfile();
        return friendshipRepository.findByReceiverAndStatus(currentUser, FriendshipStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<Friendship> getFriends() {
        UserProfile currentUser = getCurrentUserProfile();
        List<Friendship> friendships = new ArrayList<>();
        friendships.addAll(friendshipRepository.findByRequesterAndStatus(currentUser, FriendshipStatus.ACCEPTED));
        friendships.addAll(friendshipRepository.findByReceiverAndStatus(currentUser, FriendshipStatus.ACCEPTED));
        return friendships;
    }

    private UserProfile getCurrentUserProfile() {
        UUID userId = authService.getSignedInUserId();
        return userProfileRepository.findByRidepallyUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));
    }
} 