package com.inoichi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String name;
    private String geolocation;  // Include geolocation
    private int xp;  // User XP
    private int level;

    private List<TeamXpInfo> teams;  // Teams with XP details

    private int treesPlanted;  // Count of trees planted
    private int litterCleaned;  // Count of litter cleanup activities
    private int publicTransportUsed;  // Count of public transport usage

    private String token;  // JWT token (if applicable)
}
