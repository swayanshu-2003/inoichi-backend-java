package com.inoichi.controller;

import com.inoichi.dto.GenericResponse;
import com.inoichi.service.GeminiApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/cleanup")
public class GeminiController {

    private final GeminiApiService geminiApiService;

    public GeminiController(GeminiApiService geminiApiService) {
        this.geminiApiService = geminiApiService;
    }

    // Endpoint for litter cleanup: requires two images (before & after) and userId
    @PostMapping("/check-litter")
    public ResponseEntity<GenericResponse> checkCleanup(
            @RequestParam("beforeImage") MultipartFile beforeImage,
            @RequestParam("afterImage") MultipartFile afterImage,
            @RequestParam("userId") UUID userId) {
        try {
            GenericResponse result = geminiApiService.checkLitterService(beforeImage, afterImage, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse("error", "Error: " + e.getMessage()));
        }
    }

    // Endpoint for tree plantation: requires two images (before & after) and userId
    @PostMapping("/check-tree")
    public ResponseEntity<GenericResponse> checkTreePlantation(
            @RequestParam("beforeImage") MultipartFile beforeImage,
            @RequestParam("afterImage") MultipartFile afterImage,
            @RequestParam("userId") UUID userId) {
        try {
            GenericResponse result = geminiApiService.checkTreePlantation(beforeImage, afterImage, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse("error", "Error: " + e.getMessage()));
        }
    }

    // Endpoint for ticket check: requires only one image and userId
    @PostMapping("/check-ticket")
    public ResponseEntity<GenericResponse> checkTicket(
            @RequestParam("ticketImage") MultipartFile ticketImage,
            @RequestParam("userId") UUID userId) {
        try {
            GenericResponse result = geminiApiService.checkTicket(ticketImage, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse("error", "Error: " + e.getMessage()));
        }
    }
}
