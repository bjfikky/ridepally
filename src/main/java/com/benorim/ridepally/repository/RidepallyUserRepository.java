package com.benorim.ridepally.repository;

import com.benorim.ridepally.entity.RidepallyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RidepallyUserRepository extends JpaRepository<RidepallyUser, UUID> {
    Optional<RidepallyUser> findByEmailIgnoreCase(String email);
    Boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT c FROM RidepallyUser c INNER JOIN c.roles r WHERE r.name = :roleName")
    List<RidepallyUser> findByRole(@Param("roleName") String roleName);
}
