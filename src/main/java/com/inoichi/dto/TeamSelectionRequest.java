package com.inoichi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamSelectionRequest {
    private UUID userId;
    private UUID teamId;
}
