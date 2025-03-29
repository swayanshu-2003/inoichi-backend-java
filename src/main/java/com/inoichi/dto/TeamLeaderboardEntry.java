package com.inoichi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamLeaderboardEntry {
    private String teamName;
    private int xp;
    private String house;
}
