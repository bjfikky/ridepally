package com.benorim.ridepally.repository;

import com.benorim.ridepally.entity.Friendship;
import com.benorim.ridepally.entity.UserProfile;
import com.benorim.ridepally.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByRequesterAndStatus(UserProfile requester, FriendshipStatus status);
    List<Friendship> findByReceiverAndStatus(UserProfile receiver, FriendshipStatus status);
    Optional<Friendship> findByRequesterAndReceiver(UserProfile requester, UserProfile receiver);
    boolean existsByRequesterAndReceiver(UserProfile requester, UserProfile receiver);
} 