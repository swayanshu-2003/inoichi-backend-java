package com.inoichi.controller;

import com.inoichi.dto.GenericResponse;
import com.inoichi.dto.XpAwardResponse;
import com.inoichi.service.GeminiApiService;
import com.inoichi.service.AuthService;
import com.inoichi.db.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cleanup")
public class GeminiController {

    private final GeminiApiService geminiApiService;
    private final AuthService authService;

    public GeminiController(GeminiApiService geminiApiService, AuthService authService) {
        this.geminiApiService = geminiApiService;
        this.authService = authService;
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return authService.getUserByEmail(email);
    }

    @PostMapping("/check-litter")
    public ResponseEntity<GenericResponse> checkCleanup(
            @RequestParam("beforeImage") MultipartFile beforeImage,
            @RequestParam("afterImage") MultipartFile afterImage) {
        try {
            User user = getAuthenticatedUser();
            GenericResponse result = geminiApiService.checkLitterService(beforeImage, afterImage, user.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Get current user XP if possible
            Integer currentXp = null;
            try {
                User user = getAuthenticatedUser();
                currentXp = user.getXp();
            } catch (Exception ex) {
                // Ignore if we can't get current XP
            }

            return ResponseEntity.badRequest().body(new GenericResponse("error", "Error: " + e.getMessage(), currentXp));
        }
    }

    @PostMapping("/check-tree")
    public ResponseEntity<GenericResponse> checkTreePlantation(
            @RequestParam("beforeImage") MultipartFile beforeImage,
            @RequestParam("afterImage") MultipartFile afterImage) {
        try {
            User user = getAuthenticatedUser();
            GenericResponse result = geminiApiService.checkTreePlantation(beforeImage, afterImage, user.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Get current user XP if possible
            Integer currentXp = null;
            try {
                User user = getAuthenticatedUser();
                currentXp = user.getXp();
            } catch (Exception ex) {
                // Ignore if we can't get current XP
            }

            return ResponseEntity.badRequest().body(new GenericResponse("error", "Error: " + e.getMessage(), currentXp));
        }
    }

    @PostMapping("/check-ticket")
    public ResponseEntity<GenericResponse> checkTicket(
            @RequestParam("ticketImage") MultipartFile ticketImage) {
        try {
            User user = getAuthenticatedUser();
            GenericResponse result = geminiApiService.checkTicket(ticketImage, user.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Get current user XP if possible
            Integer currentXp = null;
            try {
                User user = getAuthenticatedUser();
                currentXp = user.getXp();
            } catch (Exception ex) {
                // Ignore if we can't get current XP
            }

            return ResponseEntity.badRequest().body(new GenericResponse("error", "Error: " + e.getMessage(), currentXp));
        }
    }
}