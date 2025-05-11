package com.benorim.ridepally.service;

import com.benorim.ridepally.dto.auth.SignupRequestDTO;
import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.entity.Role;
import com.benorim.ridepally.enums.RoleType;
import com.benorim.ridepally.exception.DataOwnershipException;
import com.benorim.ridepally.repository.RidepallyUserRepository;
import com.benorim.ridepally.repository.RoleRepository;
import com.benorim.ridepally.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private RidepallyUserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private UUID userId;
    private RidepallyUser ridepallyUser;
    private SignupRequestDTO signupRequest;
    private Role userRole;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ridepallyUser = new RidepallyUser();
        ridepallyUser.setId(userId);
        ridepallyUser.setEmail("test@example.com");
        ridepallyUser.setPassword("encodedPassword");

        signupRequest = new SignupRequestDTO();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");

        userRole = new Role();
        userRole.setName(RoleType.ROLE_USER.name());

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(RoleType.ROLE_USER.name()));
        userDetails = new UserDetailsImpl(
                userId,
                "test@example.com",
                "encodedPassword",
                authorities,
                true,
                true,
                true,
                true
        );

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void registerUser_WithDefaultRole_Success() {
        when(roleRepository.findByName(RoleType.ROLE_USER.name())).thenReturn(Optional.of(userRole));
        when(encoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(RidepallyUser.class))).thenReturn(ridepallyUser);

        authService.registerUser(signupRequest, null);

        verify(userRepository).save(any(RidepallyUser.class));
        verify(roleRepository).findByName(RoleType.ROLE_USER.name());
    }

    @Test
    void registerUser_WithSpecificRole_Success() {
        Role adminRole = new Role();
        adminRole.setName(RoleType.ROLE_ADMIN.name());
        when(roleRepository.findByName(RoleType.ROLE_ADMIN.name())).thenReturn(Optional.of(adminRole));
        when(encoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(RidepallyUser.class))).thenReturn(ridepallyUser);

        authService.registerUser(signupRequest, RoleType.ROLE_ADMIN);

        verify(userRepository).save(any(RidepallyUser.class));
        verify(roleRepository).findByName(RoleType.ROLE_ADMIN.name());
    }

    @Test
    void registerUser_RoleNotFound_ThrowsException() {
        when(roleRepository.findByName(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            authService.registerUser(signupRequest, null)
        );
    }

    @Test
    void getSignedInUserId_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        UUID result = authService.getSignedInUserId();

        assertEquals(userId, result);
    }

    @Test
    void getSignedInUserId_NoAuthentication_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(DataOwnershipException.class, () -> 
            authService.getSignedInUserId()
        );
    }

    @Test
    void getSignedInUserId_InvalidPrincipal_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("invalid");

        assertThrows(DataOwnershipException.class, () -> 
            authService.getSignedInUserId()
        );
    }

    @Test
    void isSuperAdmin_True() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(RoleType.ROLE_SUPER_ADMIN.name()));
        UserDetailsImpl superAdmin = new UserDetailsImpl(
                userId,
                "test@example.com",
                "encodedPassword",
                authorities,
                true,
                true,
                true,
                true
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(superAdmin);

        assertTrue(authService.isSuperAdmin());
    }

    @Test
    void isAdmin_True() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        UserDetailsImpl admin = new UserDetailsImpl(
                userId,
                "test@example.com",
                "encodedPassword",
                authorities,
                true,
                true,
                true,
                true
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(admin);

        assertTrue(authService.isAdmin());
    }

    @Test
    void isUser_True() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        assertTrue(authService.isUser());
    }

    @Test
    void isRequestMadeByLoggedInUserOrAdmin_AsSuperAdmin_True() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(RoleType.ROLE_SUPER_ADMIN.name()));
        UserDetailsImpl superAdmin = new UserDetailsImpl(
                userId,
                "test@example.com",
                "encodedPassword",
                authorities,
                true,
                true,
                true,
                true
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(superAdmin);

        assertTrue(authService.isRequestMadeByLoggedInUserOrAdmin(ridepallyUser));
    }

    @Test
    void isRequestMadeByLoggedInUserOrAdmin_AsAdmin_True() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        UserDetailsImpl admin = new UserDetailsImpl(
                userId,
                "test@example.com",
                "encodedPassword",
                authorities,
                true,
                true,
                true,
                true
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(admin);

        assertTrue(authService.isRequestMadeByLoggedInUserOrAdmin(ridepallyUser));
    }

    @Test
    void isRequestMadeByLoggedInUserOrAdmin_AsUser_True() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        assertTrue(authService.isRequestMadeByLoggedInUserOrAdmin(ridepallyUser));
    }

    @Test
    void isRequestMadeByLoggedInUser_NullUser_ThrowsException() {
        assertThrows(DataOwnershipException.class, () -> 
            authService.isRequestMadeByLoggedInUser(null)
        );
    }

    @Test
    void isRequestMadeByLoggedInUser_UserMismatch_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        RidepallyUser differentUser = new RidepallyUser();
        differentUser.setId(UUID.randomUUID());

        assertThrows(DataOwnershipException.class, () -> 
            authService.isRequestMadeByLoggedInUser(differentUser)
        );
    }
} 