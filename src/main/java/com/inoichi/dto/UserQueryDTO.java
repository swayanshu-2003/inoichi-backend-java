package com.inoichi.dto;

import com.inoichi.db.model.UserQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQueryDTO {
    private String query;
    private String extractedSymptoms;

    public UserQueryDTO(UserQuery userQuery) {
        this.query = userQuery.getQuery();
        this.extractedSymptoms = userQuery.getExtractedSymptoms();
    }
}
