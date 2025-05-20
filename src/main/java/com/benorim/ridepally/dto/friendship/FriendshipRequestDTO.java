package com.benorim.ridepally.dto.friendship;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class FriendshipRequestDTO {
    @NotNull
    private UUID receiverId;
} 