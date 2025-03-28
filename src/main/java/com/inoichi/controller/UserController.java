package com.inoichi.controller;

import com.inoichi.db.model.Team;
import com.inoichi.dto.TeamSelectionRequest;
import com.inoichi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Allows a user to select a team.
     * @param userId User's ID passed in the URL
     * @param request Team selection request containing teamId
     * @return Success message
     */
    @PostMapping("/{userId}/select-team")
    public ResponseEntity<String> selectTeam(
            @PathVariable UUID userId,
            @RequestBody TeamSelectionRequest request) {

        userService.assignTeamToUser(userId, request.getTeamId());
        return ResponseEntity.ok("Team selection successful!");
    }

}
