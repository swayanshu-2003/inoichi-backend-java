package com.inoichi.service;

import com.inoichi.db.model.Team;
import com.inoichi.db.model.User;
import com.inoichi.dto.AuthRequest;
import com.inoichi.dto.UserResponse;
import com.inoichi.repository.UserRepository;
import com.inoichi.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user and returns a response with a JWT token.
     */
    public UserResponse registerUser(AuthRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists.");
        }

        // Encrypt password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create and save user
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(hashedPassword);
        userRepository.save(newUser);

        // Generate JWT Token
        String token = jwtUtil.generateToken(request.getEmail());

        // Fetch available teams (Modify logic as needed)
        List<Team> availableTeams = new ArrayList<>(); // Replace with actual team fetching logic

        // Return response
        return new UserResponse(newUser.getId(), newUser.getEmail(), newUser.getName(), availableTeams, token);
    }
    public User getUserFromToken(String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    /**
     * Authenticates an existing user and returns a JWT token.
     */
    public String authenticateAndGenerateToken(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password.");
        }

        return jwtUtil.generateToken(email);
    }

    /**
     * Fetches user details by email.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
