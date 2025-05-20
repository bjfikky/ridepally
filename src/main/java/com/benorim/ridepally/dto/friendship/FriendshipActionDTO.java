package com.benorim.ridepally.dto.friendship;

import com.benorim.ridepally.enums.FriendshipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FriendshipActionDTO {
    @NotNull
    private FriendshipStatus action;
} 