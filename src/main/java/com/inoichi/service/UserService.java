package com.inoichi.service;

import com.inoichi.db.model.*;
import com.inoichi.dto.PublicTransportRequestDTO;
import com.inoichi.dto.TreeRequestDTO;
import com.inoichi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private TreeRepository treeRepository;
    @Autowired
    private PublicTransportRepository transportRepository;
    public UserService(
            UserRepository userRepository,
            TeamRepository teamRepository,
            UserTeamRepository userTeamRepository
    ) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.userTeamRepository = userTeamRepository;
    }

    /**
     * Retrieves all teams associated with a user.
     */
    public List<Team> getTeamsForUser(UUID userId) {
        return userTeamRepository.findByUserId(userId)
                .stream()
                .map(UserTeam::getTeam)
                .collect(Collectors.toList());
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
}



