package com.benorim.ridepally.repository;

import com.benorim.ridepally.entity.UserProfile;
import com.benorim.ridepally.entity.RidepallyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByRidepallyUser(RidepallyUser ridepallyUser);
    Optional<UserProfile> findByDisplayName(String displayName);
    boolean existsByDisplayName(String displayName);
} 