package com.benorim.ridepally.security.jwt;

import com.benorim.ridepally.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        String jwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        // 24 hours
        int jwtExpirationMs = 86400000;
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
    }

    private Authentication createMockAuthentication() {
        UUID userId = UUID.randomUUID();
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId,
                "test@example.com",
                "password",
                authorities,
                true,
                true,
                true,
                true
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        return authentication;
    }

    @Test
    void generateJwtToken_Success() {
        Authentication authentication = createMockAuthentication();
        String token = jwtUtils.generateJwtToken(authentication);
        
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length); // JWT has 3 parts
    }

    @Test
    void generateTokenFromUsername_Success() {
        String token = jwtUtils.generateTokenFromUsername("test@example.com");
        
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void getUsernameFromJwtToken_Success() {
        String token = jwtUtils.generateTokenFromUsername("test@example.com");
        String username = jwtUtils.getUsernameFromJwtToken(token);
        
        assertEquals("test@example.com", username);
    }

    @Test
    void validateJwtToken_ValidToken_Success() {
        Authentication authentication = createMockAuthentication();
        String token = jwtUtils.generateJwtToken(authentication);
        boolean isValid = jwtUtils.validateJwtToken(token);
        
        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_InvalidToken_ReturnsFalse() {
        boolean isValid = jwtUtils.validateJwtToken("invalid.token.here");
        
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_ExpiredToken_ReturnsFalse() {
        // Set a very short expiration time
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 1);
        Authentication authentication = createMockAuthentication();
        String token = jwtUtils.generateJwtToken(authentication);
        
        // Wait for token to expire
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_MalformedToken_ReturnsFalse() {
        boolean isValid = jwtUtils.validateJwtToken("malformed.token");
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_UnsupportedToken_ReturnsFalse() {
        boolean isValid = jwtUtils.validateJwtToken("unsupported.token");
        assertFalse(isValid);
    }
} 