package com.inoichi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamXpInfo {
    private UUID teamId;
    private String teamName;
    private int teamXp;
}

