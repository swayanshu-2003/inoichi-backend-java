package com.inoichi.dto;

import com.inoichi.db.model.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class HouseWithTeamsResponse {
    private UUID houseId;
    private String houseName;
    private List<TeamResponse> teams;

    @Data
    @AllArgsConstructor
    public static class TeamResponse {
        private UUID id;
        private String name;
    }

    public static HouseWithTeamsResponse fromHouse(com.inoichi.db.model.House house) {
        List<TeamResponse> teams = house.getTeams().stream()
                .map(team -> new TeamResponse(team.getId(), team.getName()))
                .collect(Collectors.toList());
        return new HouseWithTeamsResponse(house.getId(), house.getName(), teams);
    }
}
