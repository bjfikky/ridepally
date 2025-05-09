package com.benorim.ridepally.api;

import com.benorim.ridepally.dto.motorcycle.CreateMotorcycleRequestDTO;
import com.benorim.ridepally.dto.motorcycle.MotorcycleResponseDTO;
import com.benorim.ridepally.service.AuthService;
import com.benorim.ridepally.service.MotorcycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/motorcycles")
@RequiredArgsConstructor
@Slf4j
public class MotorcycleController {

    private final MotorcycleService motorcycleService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<MotorcycleResponseDTO> addMotorcycle(
            @Valid @RequestBody CreateMotorcycleRequestDTO request) {
        UUID userId = authService.getSignedInUserId();
        return new ResponseEntity<>(motorcycleService.addMotorcycle(userId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MotorcycleResponseDTO>> getUserMotorcycles() {
        UUID userId = authService.getSignedInUserId();
        return ResponseEntity.ok(motorcycleService.getUserMotorcycles(userId));
    }

    @GetMapping("/{motorcycleId}")
    public ResponseEntity<MotorcycleResponseDTO> getMotorcycle(@PathVariable Long motorcycleId) {
        return ResponseEntity.ok(motorcycleService.getMotorcycle(motorcycleId));
    }

    @DeleteMapping("/{motorcycleId}")
    public ResponseEntity<Void> deleteMotorcycle(@PathVariable Long motorcycleId) {
        motorcycleService.deleteMotorcycle(motorcycleId);
        return ResponseEntity.noContent().build();
    }
} 