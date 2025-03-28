package com.inoichi.controller;

import com.inoichi.db.model.Team;
import com.inoichi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/houses")
public class HouseController {

    @Autowired
    private UserService userService;
    @GetMapping("/{houseId}/teams")
    public ResponseEntity<List<Team>> getTeamsByHouse(@PathVariable UUID houseId) {
        List<Team> teams = userService.getTeamsForHouse(houseId);
        return ResponseEntity.ok(teams);
    }
}
