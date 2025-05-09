package com.benorim.ridepally.repository;

import com.benorim.ridepally.entity.Motorcycle;
import com.benorim.ridepally.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotorcycleRepository extends JpaRepository<Motorcycle, Long> {
    List<Motorcycle> findByUserProfile(UserProfile userProfile);
    boolean existsByUserProfileAndNickname(UserProfile userProfile, String nickname);
} 