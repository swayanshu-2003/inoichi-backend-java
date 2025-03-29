package com.inoichi.service;

import com.inoichi.db.model.*;
import com.inoichi.dto.*;
import com.inoichi.repository.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;
    private final HouseRepository houseRepository;
    private final TreeRepository treeRepository;
    private final PublicTransportRepository transportRepository;
    private final EntityManager entityManager;

    @Autowired
    public UserService(
            UserRepository userRepository,
            TeamRepository teamRepository,
            UserTeamRepository userTeamRepository,
            HouseRepository houseRepository,
            TreeRepository treeRepository,
            PublicTransportRepository transportRepository,
            EntityManager entityManager
    ) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.userTeamRepository = userTeamRepository;
        this.houseRepository = houseRepository;
        this.treeRepository = treeRepository;
        this.transportRepository = transportRepository;
        this.entityManager = entityManager;
    }

    /**
     * Retrieves all teams associated with a user.
     */
    public List<TeamWithHouseInfo> getTeamsForUser(UUID userId) {
        List<UserTeam> userTeams = userTeamRepository.findByUserId(userId);

        return userTeams.stream()
                .map(userTeam -> {
                    Team team = userTeam.getTeam();
                    String houseName = team.getHouse().getName();
                    return new TeamWithHouseInfo(team.getId(), team.getName(), houseName);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets the count of a specific activity type for a user.
     */
    public int getActivityCount(UUID userId, String activityType) {
        String query = "SELECT COUNT(ua) FROM UserActivity ua WHERE ua.user.id = :userId AND ua.activityType = :activityType";
        return entityManager.createQuery(query, Long.class)
                .setParameter("userId", userId)
                .setParameter("activityType", activityType)
                .getSingleResult()
                .intValue();
    }

    /**
     * Assigns a user to a team while ensuring they only join one team per house.
     */
    public void assignTeamToUser(UUID userId, UUID teamId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Check if user already has a team in this house
        List<UserTeam> existingAssignments = userTeamRepository.findByUserId(user.getId());
        boolean alreadyJoined = existingAssignments.stream()
                .anyMatch(ut -> ut.getTeam().getHouse().getId().equals(team.getHouse().getId()));

        if (alreadyJoined) {
            throw new RuntimeException("User already joined a team in this house!");
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeamRepository.save(userTeam);
    }

    /**
     * Retrieves houses by a set of house IDs.
     */
    public List<House> getHousesByIds(Set<UUID> houseIds) {
        return houseRepository.findAllById(houseIds);
    }

    /**
     * Adds a tree planting activity for a user.
     */
    public String addTree(TreeRequestDTO treeRequestDTO) {
        Optional<User> userOptional = userRepository.findById(treeRequestDTO.getUserId());
        if (userOptional.isEmpty()) {
            return "User not found";
        }

        Tree tree = new Tree();
        tree.setUser(userOptional.get());
        tree.setLocation(treeRequestDTO.getLocation());
        treeRepository.save(tree);

        return "Tree added successfully";
    }

    /**
     * Adds public transport usage data for a user.
     */
    public String addPublicTransport(PublicTransportRequestDTO transportRequestDTO) {
        Optional<User> userOptional = userRepository.findById(transportRequestDTO.getUserId());
        if (userOptional.isEmpty()) {
            return "User not found";
        }

        PublicTransport transport = new PublicTransport();
        transport.setUser(userOptional.get());
        transport.setTravelData(transportRequestDTO.getTravelData());
        transportRepository.save(transport);

        return "Transport details added successfully";
    }

    /**
     * Retrieves XP details for teams a user is part of.
     */
    public List<TeamXpInfo> getTeamXpForUser(UUID userId) {
        return entityManager.createQuery(
                        "SELECT new com.inoichi.dto.TeamXpInfo(t.id, t.name, t.xp) " +
                                "FROM UserTeam ut " +
                                "JOIN ut.team t " +
                                "WHERE ut.user.id = :userId",
                        TeamXpInfo.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * Retrieves the leaderboard with top 20 users and teams based on XP.
     */
    public LeaderboardResponse getLeaderboard() {
        Pageable pageable = PageRequest.of(0, 20);

        // Get top 20 users by XP
        List<User> topUsers = userRepository.findTop20UsersByXp(pageable);
        List<UserLeaderboardEntry> userLeaderboard = topUsers.stream().map(user ->
                new UserLeaderboardEntry(
                        user.getName(),
                        user.getXp(),
                        user.getTeams().stream()
                                .map(Team::getName)
                                .collect(Collectors.toList()), // Extract team names
                        user.getTeams().stream()
                                .map(team -> team.getHouse().getName())
                                .distinct()
                                .collect(Collectors.toList()) // Extract unique house names
                )
        ).collect(Collectors.toList());

        // Get top 20 teams by XP
        List<Team> topTeams = teamRepository.findTop20TeamsByXp(pageable);
        List<TeamLeaderboardEntry> teamLeaderboard = topTeams.stream().map(team ->
                new TeamLeaderboardEntry(
                        team.getName(),
                        team.getXp(),
                        team.getHouse().getName()
                )
        ).collect(Collectors.toList());

        return new LeaderboardResponse(userLeaderboard, teamLeaderboard);


    }

}
