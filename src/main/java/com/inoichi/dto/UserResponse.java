package com.inoichi.dto;

import com.inoichi.db.model.Team;
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
    private List<Team> teams;
    private String token;
}
