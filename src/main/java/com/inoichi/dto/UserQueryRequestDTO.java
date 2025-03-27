package com.inoichi.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UserQueryRequestDTO {
    private UUID userId; // This will be set from the URL
    private String query;
    private String extractedSymptoms;
}
