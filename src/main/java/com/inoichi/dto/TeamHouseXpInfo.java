package com.inoichi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamHouseXpInfo {
    private UUID teamId;
    private String teamName;
    private String houseName;
    private int xp;
}
