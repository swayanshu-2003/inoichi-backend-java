package com.inoichi.repository;

import com.inoichi.db.model.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, UUID> {
    // Find all teams a user has joined
    List<UserTeam> findByUserId(UUID userId);

    // Optional: Check if a user has already joined a team in a specific house
    boolean existsByUserIdAndTeam_House_Id(UUID userId, UUID houseId);
}
