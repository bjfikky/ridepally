package com.benorim.ridepally.dto.friendship;

import com.benorim.ridepally.enums.FriendshipStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FriendshipResponseDTO {
    private Long id;
    private UUID requesterId;
    private String requesterName;
    private UUID receiverId;
    private String receiverName;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
} 