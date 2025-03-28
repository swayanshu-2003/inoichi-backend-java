package com.inoichi.service;

import com.inoichi.db.model.Team;
import com.inoichi.db.model.User;
import com.inoichi.db.model.UserTeam;
import com.inoichi.dto.AuthRequest;
import com.inoichi.dto.TeamWithHouseInfo;
import com.inoichi.dto.UserResponse;
import com.inoichi.repository.TeamRepository;
import com.inoichi.repository.UserRepository;
import com.inoichi.repository.UserTeamRepository;
import com.inoichi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
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
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create user
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(hashedPassword);
        newUser.setGeolocation(request.getGeolocation());

        userRepository.save(newUser);

        // Assign user to multiple teams and fetch associated houseId for each team
        List<TeamWithHouseInfo> teams = request.getTeamIds().stream()
                .map(teamId -> {
                    Team team = teamRepository.findById(teamId)
                            .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

                    // Retrieve houseId from the team (since each team has a house)
                    UUID houseId = team.getHouse().getId();

                    // Create a TeamWithHouseInfo object to store team details along with houseId
                    return new TeamWithHouseInfo(team.getId(), team.getName(), houseId);
                })
                .collect(Collectors.toList());

        // Create UserTeam relations
        for (TeamWithHouseInfo teamInfo : teams) {
            Team team = teamRepository.findById(teamInfo.getId())
                    .orElseThrow(() -> new RuntimeException("Team not found: " + teamInfo.getId()));

            UserTeam userTeam = new UserTeam();
            userTeam.setUser(newUser);
            userTeam.setTeam(team);
            userTeamRepository.save(userTeam);
        }

        // Generate JWT Token
        String token = jwtUtil.generateToken(request.getEmail());

        // Return the correct list of joined teams, including houseId
        return new UserResponse(newUser.getId(), newUser.getEmail(), newUser.getName(), newUser.getGeolocation(), teams, token);
    }



    public User getUserFromToken(String token) {

        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        System.out.println("Looking up user by email: " + email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }



    /**
     * Authenticates an existing user and returns a JWT token.
     */
    public UserResponse authenticateAndGenerateToken(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password.");
        }

        // Fetch user details
        User user = getUserByEmail(email);

        // Fetch the teams associated with the user and their house details
        List<TeamWithHouseInfo> teams = userTeamRepository.findByUserId(user.getId()).stream()
                .map(userTeam -> {
                    Team team = userTeam.getTeam();
                    UUID houseId = team.getHouse().getId();
                    return new TeamWithHouseInfo(team.getId(), team.getName(), houseId);
                })
                .collect(Collectors.toList());

        // Generate JWT Token
        String token = jwtUtil.generateToken(email);

        // Return the user response with teams and token
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getGeolocation(), teams, token);
    }


    /**
     * Fetches user details by email.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
