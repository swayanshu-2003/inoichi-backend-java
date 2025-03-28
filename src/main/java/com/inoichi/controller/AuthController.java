package com.inoichi.controller;

import com.inoichi.db.model.Team;
import com.inoichi.db.model.User;
import com.inoichi.dto.AuthRequest;
import com.inoichi.dto.LoginResponse;
import com.inoichi.dto.TeamSelectionRequest;
import com.inoichi.dto.UserResponse;
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
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest request) {
        String token = authService.authenticateAndGenerateToken(request.getEmail(), request.getPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return ResponseEntity.ok().headers(headers).body(new LoginResponse("Login successful!", token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.getUserByEmail(email);
        List<Team> teams = userService.getTeamsForUser(user.getId());
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getName(), teams, null));
    }


}
