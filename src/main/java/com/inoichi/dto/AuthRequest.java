package com.inoichi.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class AuthRequest {
    private String name;
    private String email;
    private String password;
    private List<UUID> teamIds;  // Allow multiple teams
    private String geolocation;  // Store latitude & longitude
}
