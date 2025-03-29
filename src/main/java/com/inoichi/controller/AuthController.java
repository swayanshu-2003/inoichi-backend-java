package com.inoichi.controller;

import com.inoichi.db.model.Team;
import com.inoichi.db.model.User;
import com.inoichi.dto.*;
import com.inoichi.service.AuthService;
import com.inoichi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * Handles user signup and returns JWT token in response headers.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody AuthRequest request) {
        UserResponse userResponse = authService.registerUser(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + userResponse.getToken());
        return ResponseEntity.ok().headers(headers).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody AuthRequest request) {
        // Authenticate and generate token along with user teams info
        UserResponse userResponse = authService.authenticateAndGenerateToken(request.getEmail(), request.getPassword());

        // Set JWT token in response headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + userResponse.getToken());

        // Return response with user profile, teams, and JWT token
        return ResponseEntity.ok().headers(headers).body(userResponse);
    }

    /**
     * Fetches the current user profile, including the list of teams and their associated house IDs.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.getUserByEmail(email);

        // Fetch teams and their XP
        List<TeamXpInfo> teamXpInfos = userService.getTeamXpForUser(user.getId());

        // Fetch activity counts
        int treesPlanted = userService.getActivityCount(user.getId(), "TREE_PLANTATION");
        int litterCleaned = userService.getActivityCount(user.getId(), "LITTER_CLEANUP");
        int publicTransportUsed = userService.getActivityCount(user.getId(), "TICKET_VERIFICATION");
        // Calculate level
        int level = calculateLevel(user.getXp());

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getGeolocation(),
                user.getXp(),  // XP value
                level,         // Computed level
                teamXpInfos,   // Include teams with XP details
                treesPlanted,
                litterCleaned,
                publicTransportUsed,
                null // Token not needed for this endpoint
        ));

    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponse> getUserProfileById(@PathVariable UUID userId) {
        User user = authService.getUserById(userId); // Fetch user by ID

        // Fetch teams and their XP
        List<TeamXpInfo> teamXpInfos = userService.getTeamXpForUser(user.getId());

        // Fetch activity counts
        int treesPlanted = userService.getActivityCount(user.getId(), "TREE_PLANTATION");
        int litterCleaned = userService.getActivityCount(user.getId(), "LITTER_CLEANUP");
        int publicTransportUsed = userService.getActivityCount(user.getId(), "TICKET_VERIFICATION");

        // Calculate level
        int level = calculateLevel(user.getXp());

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getGeolocation(),
                user.getXp(),  // XP value
                level,         // Computed level
                teamXpInfos,   // Include teams with XP details
                treesPlanted,
                litterCleaned,
                publicTransportUsed,
                null // Token not needed for this endpoint
        ));
    }
    @GetMapping("/users/coordinates")
    public ResponseEntity<List<UserCoordinatesResponse>> getAllUserCoordinates() {
        List<UserCoordinatesResponse> userCoordinates = authService.getAllUserCoordinates();
        return ResponseEntity.ok(userCoordinates);
    }
    // Helper method to calculate level
    private int calculateLevel(int xp) {
        return (xp / 50) + 1;
    }




}
