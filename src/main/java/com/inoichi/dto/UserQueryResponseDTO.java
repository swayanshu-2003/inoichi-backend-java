package com.inoichi.dto;

import com.inoichi.db.model.UserQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserQueryResponseDTO {
    private UUID id;
    private UUID userId;
    private String query;
    private String extractedSymptoms;
    private LocalDateTime createdAt;

    public UserQueryResponseDTO(UUID id, UUID userId, String query, String extractedSymptoms, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.query = query;
        this.extractedSymptoms = extractedSymptoms;
        this.createdAt = createdAt;
    }

    // Add this constructor to accept UserQuery entity
    public UserQueryResponseDTO(UserQuery userQuery) {
        this.id = userQuery.getId();
        this.userId = userQuery.getUser().getId();
        this.query = userQuery.getQuery();
        this.extractedSymptoms = userQuery.getExtractedSymptoms();
        this.createdAt = userQuery.getCreatedAt();
    }
}
