package com.inoichi.controller;

import com.inoichi.dto.UserQueryDTO;
import com.inoichi.dto.UserQueryRequestDTO;
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
    public ResponseEntity<List<UserQueryDTO>> getUserQueries(@PathVariable UUID userId) {
        List<UserQueryDTO> queries = userQueryService.getUserQueriesByUserId(userId);
        return ResponseEntity.ok(queries);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<UserQueryDTO> createUserQuery(
            @PathVariable UUID userId,
            @RequestBody UserQueryRequestDTO request) {

        request.setUserId(userId);  // Set userId from URL
        UserQueryDTO savedQuery = userQueryService.saveUserQuery(request);
        return ResponseEntity.ok(savedQuery);
    }
}
