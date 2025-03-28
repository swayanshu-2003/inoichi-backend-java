package com.inoichi.dto;

import lombok.Data;

@Data
public class ChallengeSubmissionRequest {
    private String challengeType;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
}
