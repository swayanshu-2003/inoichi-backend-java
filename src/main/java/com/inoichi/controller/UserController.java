package com.inoichi.controller;

import com.inoichi.db.model.Team;
import com.inoichi.db.model.User;
import com.inoichi.dto.LeaderboardResponse;
import com.inoichi.dto.TeamSelectionRequest;
import com.inoichi.dto.TreeRequestDTO;
import com.inoichi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
    @PostMapping("/add-tree")
    public ResponseEntity<String> addTree(@RequestBody TreeRequestDTO treeRequestDTO) {
        String response = userService.addTree(treeRequestDTO);
        if (response.equals("User not found")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
    @GetMapping("/top-xp")
    public LeaderboardResponse getLeaderboard() {
        return userService.getLeaderboard();
    }

}
