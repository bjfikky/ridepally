package com.benorim.ridepally.repository;

import com.benorim.ridepally.entity.RidepallyUser;
import com.benorim.ridepally.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByRidepallyUser(RidepallyUser ridepallyUser);
    Optional<UserProfile> findByDisplayNameIgnoreCase(String displayName);
    boolean existsByDisplayNameIgnoreCase(String displayName);
    List<UserProfile> findByLocationCityIgnoreCaseAndLocationStateIgnoreCase(String city, String state);
    Optional<UserProfile> findByRidepallyUserId(UUID userId);
}