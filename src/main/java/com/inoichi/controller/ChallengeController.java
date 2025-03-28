package com.inoichi.controller;

import com.inoichi.db.model.ChallengeSubmission;
import com.inoichi.dto.ChallengeSubmissionRequest;
import com.inoichi.service.ChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    /**
     * Submits a challenge for a given user.
     */
    @PostMapping("/{userId}/submit")
    public ResponseEntity<ChallengeSubmission> submitChallenge(
            @PathVariable UUID userId,
            @RequestBody ChallengeSubmissionRequest request) {

        ChallengeSubmission submission = challengeService.submitChallenge(userId, request);
        return ResponseEntity.ok(submission);
    }

    /**
     * Fetch all challenge submissions for a specific user.
     */
    @GetMapping("/{userId}/submissions")
    public ResponseEntity<List<ChallengeSubmission>> getUserSubmissions(@PathVariable UUID userId) {
        List<ChallengeSubmission> submissions = challengeService.getUserSubmissions(userId);
        return ResponseEntity.ok(submissions);
    }
}
