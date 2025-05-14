package com.benorim.ridepally.security.services;

import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.entity.Role;
import com.benorim.ridepally.enums.RoleType;
import com.benorim.ridepally.repository.RidepallyUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private RidepallyUserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private RidepallyUser testUser;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        Set<Role> roles = new HashSet<>();

        Role userRole = new Role();
        userRole.setName(RoleType.ROLE_USER.name());
        roles.add(userRole);

        testUser = new RidepallyUser();
        testUser.setId(userId);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRoles(roles);
        testUser.setEnabled(true);
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername("nonexistent@example.com")
        );
    }

    @Test
    void loadUserByUsername_UserDisabled_ReturnsDisabledUserDetails() {
        testUser.setEnabled(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_UserLocked_ReturnsLockedUserDetails() {
        testUser.setAccountNonLocked(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    void loadUserByUsername_UserExpired_ReturnsExpiredUserDetails() {
        testUser.setAccountNonExpired(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertFalse(userDetails.isAccountNonExpired());
    }

    @Test
    void loadUserByUsername_UserCredentialsExpired_ReturnsCredentialsExpiredUserDetails() {
        testUser.setCredentialsNonExpired(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertFalse(userDetails.isCredentialsNonExpired());
    }
} 