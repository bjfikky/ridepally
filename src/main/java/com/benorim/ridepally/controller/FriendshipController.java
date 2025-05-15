package com.benorim.ridepally.controller;

import com.benorim.ridepally.dto.FriendshipActionDTO;
import com.benorim.ridepally.dto.FriendshipRequestDTO;
import com.benorim.ridepally.dto.FriendshipResponseDTO;
import com.benorim.ridepally.entity.Friendship;
import com.benorim.ridepally.mapper.FriendshipMapper;
import com.benorim.ridepally.service.FriendshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping("/request")
    public ResponseEntity<FriendshipResponseDTO> sendFriendRequest(@Valid @RequestBody FriendshipRequestDTO request) {
        Friendship friendship = friendshipService.sendFriendRequest(request.getReceiverId());
        return ResponseEntity.ok(FriendshipMapper.toResponseDTO(friendship));
    }

    @PostMapping("/{friendshipId}/respond")
    public ResponseEntity<FriendshipResponseDTO> respondToFriendRequest(
            @PathVariable Long friendshipId,
            @Valid @RequestBody FriendshipActionDTO action) {
        Friendship friendship = friendshipService.respondToFriendRequest(friendshipId, action);
        return ResponseEntity.ok(FriendshipMapper.toResponseDTO(friendship));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendshipResponseDTO>> getPendingFriendRequests() {
        List<Friendship> friendships = friendshipService.getPendingFriendRequests();
        List<FriendshipResponseDTO> response = friendships.stream()
                .map(FriendshipMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FriendshipResponseDTO>> getFriends() {
        List<Friendship> friendships = friendshipService.getFriends();
        List<FriendshipResponseDTO> response = friendships.stream()
                .map(FriendshipMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
} 