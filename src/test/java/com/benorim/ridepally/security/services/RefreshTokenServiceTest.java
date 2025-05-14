package com.benorim.ridepally.security.services;

import com.benorim.ridepally.entity.RefreshToken;
import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.exception.TokenRefreshException;
import com.benorim.ridepally.repository.RefreshTokenRepository;
import com.benorim.ridepally.repository.RidepallyUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RidepallyUserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private UUID userId;
    private RidepallyUser testUser;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new RidepallyUser();
        testUser.setId(userId);
        testUser.setEmail("test@example.com");

        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRidepallyUser(testUser);
        // 24 hours
        long refreshTokenDurationMs = 86400000;
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", refreshTokenDurationMs);
    }

    @Test
    void findByToken_ExistingToken_ReturnsToken() {
        when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenService.findByToken(refreshToken.getToken());

        assertTrue(result.isPresent());
        assertEquals(refreshToken.getToken(), result.get().getToken());
        verify(refreshTokenRepository).findByToken(refreshToken.getToken());
    }

    @Test
    void findByToken_NonExistingToken_ReturnsEmpty() {
        String nonExistingToken = "non-existing-token";
        when(refreshTokenRepository.findByToken(nonExistingToken))
                .thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.findByToken(nonExistingToken);

        assertTrue(result.isEmpty());
        verify(refreshTokenRepository).findByToken(nonExistingToken);
    }

    @Test
    void createRefreshToken_ValidUser_CreatesAndSavesToken() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken result = refreshTokenService.createRefreshToken(userId);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertNotNull(result.getExpiryDate());
        assertEquals(testUser, result.getRidepallyUser());
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            refreshTokenService.createRefreshToken(userId)
        );
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_ValidToken_ReturnsToken() {
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(result);
        assertEquals(refreshToken, result);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_ExpiredToken_ThrowsException() {
        refreshToken.setExpiryDate(Instant.now().minusMillis(1000)); // Set to past

        assertThrows(TokenRefreshException.class, () -> 
            refreshTokenService.verifyExpiration(refreshToken)
        );
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void deleteByUserId_DeletesAllUserTokens() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.deleteByRidepallyUser(testUser)).thenReturn(1);

        int deletedCount = refreshTokenService.deleteByUserId(userId);

        assertEquals(1, deletedCount);
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).deleteByRidepallyUser(testUser);
    }

    @Test
    void deleteByUserId_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            refreshTokenService.deleteByUserId(userId)
        );
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository, never()).deleteByRidepallyUser(any(RidepallyUser.class));
    }
} 