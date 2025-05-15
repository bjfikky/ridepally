package com.benorim.ridepally.mapper;

import com.benorim.ridepally.dto.friendship.FriendshipResponseDTO;
import com.benorim.ridepally.entity.Friendship;

public final class FriendshipMapper {
    
    private FriendshipMapper() {
        // Private constructor to prevent instantiation
    }
    
    public static FriendshipResponseDTO toResponseDTO(Friendship friendship) {
        if (friendship == null) {
            return null;
        }

        return FriendshipResponseDTO.builder()
                .id(friendship.getId())
                .requesterId(friendship.getRequester().getRidepallyUser().getId())
                .requesterName(friendship.getRequester().getDisplayName())
                .receiverId(friendship.getReceiver().getRidepallyUser().getId())
                .receiverName(friendship.getReceiver().getDisplayName())
                .status(friendship.getStatus())
                .createdAt(friendship.getCreatedAt())
                .build();
    }
} 