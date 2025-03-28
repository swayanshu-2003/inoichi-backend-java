package com.inoichi.service;

import com.inoichi.db.model.ChallengeSubmission;
import com.inoichi.db.model.User;
import com.inoichi.dto.ChallengeSubmissionRequest;
import com.inoichi.repository.ChallengeSubmissionRepository;
import com.inoichi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChallengeService {

    private final ChallengeSubmissionRepository challengeSubmissionRepository;
    private final UserRepository userRepository;

    public ChallengeService(ChallengeSubmissionRepository challengeSubmissionRepository, UserRepository userRepository) {
        this.challengeSubmissionRepository = challengeSubmissionRepository;
        this.userRepository = userRepository;
    }


    /**
     * Submits a challenge for a user.
     */
    public ChallengeSubmission submitChallenge(UUID userId, ChallengeSubmissionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChallengeSubmission submission = new ChallengeSubmission();
        submission.setUser(user);
        submission.setChallengeType(request.getChallengeType());
        submission.setImageUrl(request.getImageUrl());
        submission.setLatitude(request.getLatitude());
        submission.setLongitude(request.getLongitude());
        submission.setVerified(false);
        submission.setPoints(0);

        return challengeSubmissionRepository.save(submission);
    }
    public List<ChallengeSubmission> getUserSubmissions(UUID userId) {
        return challengeSubmissionRepository.findByUserId(userId);
    }

}
