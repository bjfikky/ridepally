package com.benorim.ridepally.config;

import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.entity.Role;
import com.benorim.ridepally.enums.RoleType;
import com.benorim.ridepally.repository.RidepallyUserRepository;
import com.benorim.ridepally.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    public static final String SUPER_ADMIN_EMAIL = "superadmin@ridepally.com";
    private final RoleRepository roleRepository;
    private final RidepallyUserRepository userRepository;
    private final PasswordEncoder encoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("Initializing roles...");
            createRoleIfNotExists(RoleType.ROLE_USER.name());
            createRoleIfNotExists(RoleType.ROLE_ADMIN.name());
            createRoleIfNotExists(RoleType.ROLE_SUPER_ADMIN.name());
            createSuperAdminUser();
            log.info("Data initialization completed.");
        };
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder().name(roleName).build();
            roleRepository.save(role);
            log.info("Created role: {}", roleName);
        }
    }

    private void createSuperAdminUser() {
        if (!userRepository.existsByEmail(SUPER_ADMIN_EMAIL)) {
            Role superAdminRole = roleRepository.findByName(RoleType.ROLE_SUPER_ADMIN.name())
                    .orElseThrow(() -> new IllegalStateException("ROLE_SUPER_ADMIN does not exist"));

            RidepallyUser user = RidepallyUser.builder()
                    .email(SUPER_ADMIN_EMAIL)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .enabled(true)
                    .credentialsNonExpired(true)
                    .roles(Set.of(superAdminRole))
                    // TODO: Can't be a plain text
                    .password(encoder.encode("password1234"))
                    .build();
            userRepository.save(user);
            log.info("Created super admin user: {}", SUPER_ADMIN_EMAIL);
        }
    }
}