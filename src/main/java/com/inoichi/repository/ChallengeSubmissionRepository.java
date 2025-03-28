package com.inoichi.repository;

import com.inoichi.db.model.ChallengeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeSubmissionRepository extends JpaRepository<ChallengeSubmission, UUID> {
    List<ChallengeSubmission> findByUserId(UUID userId);
}
