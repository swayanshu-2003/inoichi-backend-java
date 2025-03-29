package com.inoichi.dto;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLeaderboardEntry {
    private String name;
    private int xp;
    private List<String> teams;
    private List<String> houses;
}
