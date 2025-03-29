package com.inoichi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XpAwardResponse {
    private String status;
    private String message;
    private ActivityInfo activity;
    private UserXpInfo user;
    private TeamXpInfo team;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ActivityInfo {
        private String type;
        private UUID activityId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserXpInfo {
        private UUID id;
        private int previousXp;
        private int newXp;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TeamXpInfo {
        private UUID id;
        private int previousXp;
        private int newXp;
    }
}
