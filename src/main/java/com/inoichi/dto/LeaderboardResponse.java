package com.inoichi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardResponse {
    private List<UserLeaderboardEntry> userLeaderboard;
    private List<TeamLeaderboardEntry> teamLeaderboard;
}
