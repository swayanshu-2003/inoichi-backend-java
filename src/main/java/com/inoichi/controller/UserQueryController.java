package com.inoichi.controller;

import com.inoichi.dto.UserQueryDTO;
import com.inoichi.dto.UserQueryRequestDTO;
import com.inoichi.dto.UserQueryResponseDTO;
import com.inoichi.service.UserQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-queries")
public class UserQueryController {
    private final UserQueryService userQueryService;

    public UserQueryController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserQueryResponseDTO>> getUserQueries(@PathVariable UUID userId) {
        System.out.println("📌 Fetching all queries for userId: " + userId);

        List<UserQueryResponseDTO> queries = userQueryService.getAllQueriesForUser(userId);
        return ResponseEntity.ok(queries);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<UserQueryResponseDTO> addUserQuery(
            @PathVariable UUID userId,
            @RequestBody UserQueryRequestDTO requestDTO) {

        System.out.println("📌 Received POST request for userId: " + userId);
        System.out.println("📌 Query: " + requestDTO.getQuery());
        System.out.println("📌 Symptoms: " + requestDTO.getExtractedSymptoms());

        UserQueryResponseDTO savedQuery = userQueryService.saveUserQuery(userId, requestDTO);
        return ResponseEntity.ok(savedQuery);
    }

}
