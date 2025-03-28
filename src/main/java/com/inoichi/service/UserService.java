package com.inoichi.service;

import com.inoichi.db.model.Team;
import com.inoichi.db.model.User;
import com.inoichi.db.model.UserTeam;
import com.inoichi.dto.TeamSelectionRequest;
import com.inoichi.repository.TeamRepository;
import com.inoichi.repository.UserRepository;
import com.inoichi.repository.UserTeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;

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
     * Assigns a user to a team while ensuring they only join one team per house.
     */
    public void assignTeamToUser(TeamSelectionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team team = teamRepository.findById(request.getTeamId())
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
    public List<Team> getTeamsForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getTeams(); // This will retrieve teams using UserTeam relationships
    }

}
