package com.inoichi.service;

import com.inoichi.db.model.Team;
import com.inoichi.db.model.User;
import com.inoichi.db.model.UserTeam;
import com.inoichi.dto.*;
import com.inoichi.repository.TeamRepository;
import com.inoichi.repository.UserRepository;
import com.inoichi.repository.UserTeamRepository;
import com.inoichi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    private UserTeamRepository userTeamRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
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

    public UserResponse registerUser(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(hashedPassword);
        newUser.setGeolocation(request.getGeolocation());
        newUser.setXp(0); // New users start with 0 XP

        userRepository.save(newUser);

        String token = jwtUtil.generateToken(request.getEmail());

        int level = calculateLevel(0); // Since XP is 0, level should be 1

        return new UserResponse(
                newUser.getId(),
                newUser.getEmail(),
                newUser.getName(),
                newUser.getGeolocation(),
                0,    // XP value
                level, // Computed level
                Collections.emptyList(),
                0,
                0,
                0,
                token
        );
    }

    public User getUserFromToken(String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponse authenticateAndGenerateToken(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password.");
        }

        User user = getUserByEmail(email);
        String token = jwtUtil.generateToken(email);

        int level = calculateLevel(user.getXp()); // Calculate level based on user's XP

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getGeolocation(),
                user.getXp(), // XP value
                level,        // Computed level
                Collections.emptyList(),
                0,
                0,
                0,
                token
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserById(UUID uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserCoordinatesResponse> getAllUserCoordinates() {
        List<User> users = userRepository.findAll();
        List<UserCoordinatesResponse> userCoordinates = users.stream()
                .map(user -> new UserCoordinatesResponse(user.getId(), user.getName(), user.getGeolocation()))
                .collect(Collectors.toList());

        int incrementFactor = 1;
        for (int i = 0; i < userCoordinates.size(); i++) {
            for (int j = i + 1; j < userCoordinates.size(); j++) {
                UserCoordinatesResponse user1 = userCoordinates.get(i);
                UserCoordinatesResponse user2 = userCoordinates.get(j);

                if (areCoordinatesClose(user1.getGeolocation(), user2.getGeolocation())) {
                    user1.setGeolocation(addFiveToCoordinates(user1.getGeolocation(), incrementFactor++));
                    user2.setGeolocation(addFiveToCoordinates(user2.getGeolocation(), incrementFactor++));

                    updateUserCoordinates(user1.getUserId(), user1.getGeolocation());
                    updateUserCoordinates(user2.getUserId(), user2.getGeolocation());
                }
            }
        }
        return userCoordinates;
    }

    private boolean areCoordinatesClose(String geo1, String geo2) {
        try {
            String[] coords1 = geo1.split(",");
            String[] coords2 = geo2.split(",");
            double lat1 = Double.parseDouble(coords1[0]);
            double lon1 = Double.parseDouble(coords1[1]);
            double lat2 = Double.parseDouble(coords2[0]);
            double lon2 = Double.parseDouble(coords2[1]);

            double distance = haversine(lat1, lon1, lat2, lon2);
            return distance < 0.1;
        } catch (Exception e) {
            return false;
        }
    }

    private void updateUserCoordinates(UUID userId, String newGeolocation) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setGeolocation(newGeolocation);
            userRepository.save(user);
        }
    }

    private String addFiveToCoordinates(String geolocation, int incrementFactor) {
        try {
            String[] coords = geolocation.split(",");
            double lat = Double.parseDouble(coords[0]) + (5 * incrementFactor);
            double lon = Double.parseDouble(coords[1]) + (5 * incrementFactor);
            return lat + "," + lon;
        } catch (Exception e) {
            return geolocation;
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Helper method to calculate level based on XP
    private int calculateLevel(int xp) {
        return (xp / 50) + 1;
    }
}
