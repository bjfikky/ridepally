package com.benorim.ridepally.controller;

import com.benorim.ridepally.dto.motorcycle.CreateMotorcyclesRequestDTO;
import com.benorim.ridepally.dto.motorcycle.CreateMotorcycleRequestDTO;
import com.benorim.ridepally.dto.motorcycle.MotorcycleResponseDTO;
import com.benorim.ridepally.entity.Motorcycle;
import com.benorim.ridepally.mapper.MotorcycleMapper;
import com.benorim.ridepally.service.AuthService;
import com.benorim.ridepally.service.MotorcycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<MotorcycleResponseDTO> addMotorcycle(@Valid @RequestBody CreateMotorcycleRequestDTO request) {
        UUID userId = authService.getSignedInUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("Adding motorcycle for user: {}", userId);
        Motorcycle motorcycle = motorcycleService.addMotorcycle(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MotorcycleMapper.toResponseDTO(motorcycle));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<MotorcycleResponseDTO>> addMotorcycles(@Valid @RequestBody CreateMotorcyclesRequestDTO request) {
        UUID userId = authService.getSignedInUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("Adding {} motorcycles for user: {}", request.getMotorcycles().size(), userId);
        List<Motorcycle> motorcycles = motorcycleService.addMotorcycles(userId, request.getMotorcycles());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(motorcycles.stream()
                        .map(MotorcycleMapper::toResponseDTO)
                        .toList());
    }

    @GetMapping
    public ResponseEntity<List<MotorcycleResponseDTO>> getUserMotorcycles() {
        UUID userId = authService.getSignedInUserId();
        List<Motorcycle> motorcycles = motorcycleService.getUserMotorcycles(userId);
        return ResponseEntity.ok(motorcycles.stream()
                .map(MotorcycleMapper::toResponseDTO)
                .toList());
    }

    @GetMapping("/{motorcycleId}")
    public ResponseEntity<MotorcycleResponseDTO> getMotorcycle(@PathVariable Long motorcycleId) {
        Motorcycle motorcycle = motorcycleService.getMotorcycle(motorcycleId);
        return ResponseEntity.ok(MotorcycleMapper.toResponseDTO(motorcycle));
    }

    @DeleteMapping("/{motorcycleId}")
    public ResponseEntity<Void> deleteMotorcycle(@PathVariable Long motorcycleId) {
        log.info("Deleting motorcycle with id: {}", motorcycleId);
        motorcycleService.deleteMotorcycle(motorcycleId);
        return ResponseEntity.noContent().build();
    }
} 