package com.inoichi.service;

import com.inoichi.db.model.*;
import com.inoichi.dto.*;
import com.inoichi.repository.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
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
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private TreeRepository treeRepository;
    private EntityManager entityManager;

    @Autowired
    private PublicTransportRepository transportRepository;
    public UserService(
            UserRepository userRepository,
            TeamRepository teamRepository,
            UserTeamRepository userTeamRepository,
            EntityManager entityManager
    ) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.userTeamRepository = userTeamRepository;
        this.entityManager = entityManager;
    }

    /**
     * Retrieves all teams associated with a user.
     */
    public List<TeamWithHouseInfo> getTeamsForUser(UUID userId) {
        // Fetch the user-team relationships based on userId
        List<UserTeam> userTeams = userTeamRepository.findByUserId(userId);

        // Map these relationships to a list of TeamWithHouseInfo DTOs
        return userTeams.stream()
                .map(userTeam -> {
                    Team team = userTeam.getTeam();  // Get the associated team
                    String houseName = team.getHouse().getName();  // Get the associated house name
                    return new TeamWithHouseInfo(team.getId(), team.getName(), houseName);  // Return the TeamWithHouseInfo with house name
                })
                .collect(Collectors.toList());
    }
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

        // Save user-team mapping
        UserTeam userTeam = new UserTeam();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeamRepository.save(userTeam);
    }
    public List<House> getHousesByIds(Set<UUID> houseIds) {
        return houseRepository.findAllById(houseIds);
    }
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
    public String addPublicTransport(PublicTransportRequestDTO transportRequestDTO) {
        Optional<User> userOptional = userRepository.findById(transportRequestDTO.getUserId());
        if (userOptional.isEmpty()) {
            return null;
        }

        PublicTransport transport = new PublicTransport();
        transport.setUser(userOptional.get());
        transport.setTravelData(transportRequestDTO.getTravelData());
        transport = transportRepository.save(transport);

        return "Transport details added successfully";
    }
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



}



