package com.inoichi.controller;

import com.inoichi.db.model.House;
import com.inoichi.dto.HouseWithTeamsResponse;
import com.inoichi.service.AuthService;
import com.inoichi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/houses")
public class HouseController {

    @Autowired
    private UserService userService;

    @GetMapping("/teams")
    public ResponseEntity<List<House>> getHousesWithTeams(@RequestParam Set<UUID> houseIds) {
        List<House> houses = userService.getHousesByIds(houseIds);
        return ResponseEntity.ok(houses);
    }
}
