package com.benorim.ridepally.service;

import com.benorim.ridepally.dto.auth.SignupRequestDTO;
import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.entity.Role;
import com.benorim.ridepally.enums.ErrorCode;
import com.benorim.ridepally.enums.RoleType;
import com.benorim.ridepally.exception.DataOwnershipException;
import com.benorim.ridepally.repository.RidepallyUserRepository;
import com.benorim.ridepally.repository.RoleRepository;
import com.benorim.ridepally.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final RidepallyUserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    public void registerUser(SignupRequestDTO signUpRequest, RoleType roleType) {
        // Create new user's account
        RidepallyUser user = RidepallyUser.builder()
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();


        Set<Role> roles = new HashSet<>();

        if (roleType == null) {
            Role userRole = roleRepository.findByName(RoleType.ROLE_USER.name())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            Role role = roleRepository.findByName(roleType.name())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(role);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public UUID getSignedInUserId() {
        // Get the current authentication
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            log.error("No authentication found");
            throw new DataOwnershipException(ErrorCode.UNAUTHORIZED_ACCESS, "No authentication found");
        }

        // Get the principal
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if principal is UserDetailsImpl
        if (!(principal instanceof UserDetailsImpl currentUser)) {
            log.error("Principal is not a UserDetailsImpl: {}", principal.getClass().getName());
            throw new DataOwnershipException(ErrorCode.UNAUTHORIZED_ACCESS,"Principal is not a UserDetailsImpl");
        }

        return currentUser.getId();
    }

    public boolean isSuperAdmin() {
        return adminType(RoleType.ROLE_SUPER_ADMIN);
    }

    public boolean isAdmin() {
        return adminType(RoleType.ROLE_ADMIN);
    }

    public boolean isUser() {
        return adminType(RoleType.ROLE_USER);
    }

    public boolean isRequestMadeByLoggedInUserOrAdmin(RidepallyUser user) {
        return isSuperAdmin() || isAdmin() || isRequestMadeByLoggedInUser(user);
    }

    public boolean isRequestMadeByLoggedInUser(RidepallyUser user) {
        if (user == null) {
            throw new DataOwnershipException(ErrorCode.UNAUTHORIZED_ACCESS ,"User not found");
        }
        UUID signedInUserId = getSignedInUserId();
        if (signedInUserId == null) {
            log.error("User is not signed in");
            throw new DataOwnershipException(ErrorCode.UNAUTHORIZED_ACCESS ,"User is not signed in");
        }

        if (!signedInUserId.equals(user.getId())) {
            log.error("User id mismatch");
            throw new DataOwnershipException(ErrorCode.UNAUTHORIZED_ACCESS, "User id mismatch");
        }
        return true;
    }

    private static boolean adminType(RoleType roleType) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleType.name()));
    }
}
