package com.inoichi.controller;

import com.inoichi.dto.GenericResponse;
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

    // Endpoint for litter cleanup: requires two images (before & after)
    @PostMapping("/check-litter")
    public ResponseEntity<GenericResponse> checkCleanup(
            @RequestParam("beforeImage") MultipartFile beforeImage,
            @RequestParam("afterImage") MultipartFile afterImage) {
        try {
            User user = getAuthenticatedUser();
            GenericResponse result = geminiApiService.checkLitterService(beforeImage, afterImage, user.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse("error", "Error: " + e.getMessage()));
        }
    }

    // Endpoint for tree plantation: requires two images (before & after)
    @PostMapping("/check-tree")
    public ResponseEntity<GenericResponse> checkTreePlantation(
            @RequestParam("beforeImage") MultipartFile beforeImage,
            @RequestParam("afterImage") MultipartFile afterImage) {
        try {
            User user = getAuthenticatedUser();
            GenericResponse result = geminiApiService.checkTreePlantation(beforeImage, afterImage, user.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse("error", "Error: " + e.getMessage()));
        }
    }

    // Endpoint for ticket check: requires only one image
    @PostMapping("/check-ticket")
    public ResponseEntity<GenericResponse> checkTicket(
            @RequestParam("ticketImage") MultipartFile ticketImage) {
        try {
            User user = getAuthenticatedUser();
            GenericResponse result = geminiApiService.checkTicket(ticketImage, user.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse("error", "Error: " + e.getMessage()));
        }
    }
}
