package com.inoichi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inoichi.db.model.Team;
import com.inoichi.db.model.User;
import com.inoichi.db.model.UserActivity;
import com.inoichi.dto.GenericResponse;
import com.inoichi.dto.XpAwardResponse;
import com.inoichi.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class GeminiApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.api.base_url}")
    private String baseUrl;

    public GeminiApiService(RestTemplate restTemplate, ObjectMapper objectMapper,
                            UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        log.info("GeminiApiService initialized with repositories and configs");
    }

    // ------------------------------
    // Public methods for verification
    // ------------------------------

    // 🔹 Check Litter Cleanup (requires two images)
    // 🔹 Check Litter Cleanup (requires two images)
    @Transactional
    public GenericResponse checkLitterService(MultipartFile beforeImage, MultipartFile afterImage, UUID userId) throws Exception {

        log.info("Starting litter cleanup check for user: {}", userId);
        log.debug("Before image name: {}, size: {}", beforeImage.getOriginalFilename(), beforeImage.getSize());
        log.debug("After image name: {}, size: {}", afterImage.getOriginalFilename(), afterImage.getSize());

        try {
            Map<String, String> uploadedFiles = uploadMultipleFiles(beforeImage, afterImage);
            log.info("Successfully uploaded both images for litter check");

            String beforeUri = uploadedFiles.get("before");
            String afterUri = uploadedFiles.get("after");
            log.debug("Before URI: {}", beforeUri);
            log.debug("After URI: {}", afterUri);

            String prompt = "Compare the two images. The first image shows an area with litter, and the second image shows the same area. Please analyze the second image and confirm if the litter has been cleaned up in that area";
            log.info("Sending litter cleanup prompt to Gemini API");

            JsonNode response = generateContent(beforeUri, afterUri, prompt);
            log.info("Received response from Gemini API for litter check");

            String aiResponse = extractAiResponse(response);
            log.info("AI Response for litter check: {}", aiResponse);

            if (aiResponse.toLowerCase().contains("yes")) {
                XpAwardResponse xpResponse = addXpToUser(userId, 10, "LITTER_CLEANUP"); // Award 10 XP

                // Get the updated XP value from the response
                Integer newUserXp = xpResponse.getUser() != null ? xpResponse.getUser().getNewXp() : null;

                return GenericResponse.builder()
                        .status("success")
                        .message("Litter has been cleaned! You earned +10 XP!")
                        .userXp(newUserXp) // Include the updated XP value
                        .build();
            } else {
                // For failure cases, get the current XP
                User user = userRepository.findById(userId).orElse(null);
                Integer currentXp = user != null ? user.getXp() : null;

                return GenericResponse.builder()
                        .status("failure")
                        .message("No significant changes detected. Try again!")
                        .userXp(currentXp) // Include current XP even for failures
                        .build();
            }
        } catch (Exception e) {
            log.error("Error in litter cleanup check process: {}", e.getMessage(), e);
            return GenericResponse.builder()
                    .status("error")
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }
    @Transactional
    public GenericResponse checkTicket(MultipartFile ticketImage, UUID userId) throws Exception {
        log.info("Starting ticket verification check for user: {}", userId);

        try {
            String ticketUri = uploadFileToGoogle(ticketImage);

            String prompt = "Analyze this ticket image and verify: 1. Is this a valid public transport ticket (Bus/Train/Metro)? 2. Does it have a valid date? 3. Is the user contributing to CO₂ reduction by using public transport?";

            JsonNode response = generateContent(ticketUri, prompt);
            String aiResponse = extractAiResponse(response);

            if (aiResponse.toLowerCase().contains("yes")) {
                XpAwardResponse xpResponse = addXpToUser(userId, 30, "TICKET_VERIFICATION"); // Award 30 XP

                // Get the updated XP value from the response
                Integer newUserXp = xpResponse.getUser() != null ? xpResponse.getUser().getNewXp() : null;

                return GenericResponse.builder()
                        .status("success")
                        .message("Public transport used! You earned +30 XP!")
                        .userXp(newUserXp) // Include the updated XP value
                        .build();
            } else {
                // For failure cases, you may want to get the current XP
                User user = userRepository.findById(userId).orElse(null);
                Integer currentXp = user != null ? user.getXp() : null;

                return GenericResponse.builder()
                        .status("failure")
                        .message("Invalid ticket. Try again!")
                        .userXp(currentXp) // Include current XP even for failures
                        .build();
            }
        } catch (Exception e) {
            log.error("Error in ticket verification process: {}", e.getMessage(), e);
            return GenericResponse.builder()
                    .status("error")
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

    // 🔹 Check Tree Plantation (requires two images)
    @Transactional
    public GenericResponse checkTreePlantation(MultipartFile beforeImage, MultipartFile afterImage, UUID userId) throws Exception {
        log.info("Starting tree plantation check for user: {}", userId);

        try {
            Map<String, String> uploadedFiles = uploadMultipleFiles(beforeImage, afterImage);
            String beforeUri = uploadedFiles.get("before");
            String afterUri = uploadedFiles.get("after");

            String prompt = "Compare these two images. Is there a noticeable difference between the first and second images specifically in terms of tree plantation (e.g., trees planted in the second image)?";

            JsonNode response = generateContent(beforeUri, afterUri, prompt);
            String aiResponse = extractAiResponse(response);

            if (aiResponse.toLowerCase().contains("yes")) {
                XpAwardResponse xpResponse = addXpToUser(userId, 20, "TREE_PLANTATION"); // Award 20 XP

                // Get the updated XP value from the response
                Integer newUserXp = xpResponse.getUser() != null ? xpResponse.getUser().getNewXp() : null;

                return GenericResponse.builder()
                        .status("success")
                        .message("Tree has been planted! You earned +20 XP!")
                        .userXp(newUserXp) // Include the updated XP value
                        .build();
            } else {
                // For failure cases, get the current XP
                User user = userRepository.findById(userId).orElse(null);
                Integer currentXp = user != null ? user.getXp() : null;

                return GenericResponse.builder()
                        .status("failure")
                        .message("No significant changes detected. Try again!")
                        .userXp(currentXp) // Include current XP even for failures
                        .build();
            }
        } catch (Exception e) {
            log.error("Error in tree plantation check process: {}", e.getMessage(), e);
            return GenericResponse.builder()
                    .status("error")
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }


    // ------------------------------
    // Helper methods
    // ------------------------------

    // Overloaded: Generate Content using Gemini AI (for two images)
    private JsonNode generateContent(String beforeUri, String afterUri, String prompt) throws Exception {
        log.info("Generating content with two images");
        String url = String.format("%s/v1beta/models/gemini-1.5-flash:generateContent?key=%s", baseUrl, apiKey);
        log.debug("API URL: {}", url);

        String requestBody = String.format(
                "{ \"contents\": [ { \"role\": \"user\", \"parts\": [ " +
                        "{ \"text\": \"%s\" }, " +
                        "{ \"fileData\": { \"fileUri\": \"%s\" } }, " +
                        "{ \"fileData\": { \"fileUri\": \"%s\" } } " +
                        " ] } ] }",
                prompt, beforeUri, afterUri
        );
        log.debug("Request body prepared with prompt and two image URIs");

        return sendRequestToGemini(url, requestBody);
    }

    // Overloaded: Generate Content using Gemini AI (for one image)
    private JsonNode generateContent(String imageUri, String prompt) throws Exception {
        log.info("Generating content with one image");
        String url = String.format("%s/v1beta/models/gemini-1.5-flash:generateContent?key=%s", baseUrl, apiKey);
        log.debug("API URL: {}", url);

        String requestBody = String.format(
                "{ \"contents\": [ { \"role\": \"user\", \"parts\": [ " +
                        "{ \"text\": \"%s\" }, " +
                        "{ \"fileData\": { \"fileUri\": \"%s\" } } " +
                        " ] } ] }",
                prompt, imageUri
        );
        log.debug("Request body prepared with prompt and one image URI");

        return sendRequestToGemini(url, requestBody);
    }

    // Send request to Gemini API and return parsed JSON response
    private JsonNode sendRequestToGemini(String url, String requestBody) throws Exception {
        log.info("Sending request to Gemini API");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        try {
            log.debug("Executing HTTP POST to Gemini API");
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            log.debug("Received HTTP status: {}", response.getStatusCode());

            if (response.getBody() == null) {
                log.error("Empty response received from Gemini API");
                throw new Exception("Empty response from Gemini API.");
            }

            log.info("Successfully received response from Gemini API");
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            log.error("Error sending request to Gemini API: {}", e.getMessage(), e);
            throw new Exception("Gemini API request failed: " + e.getMessage());
        }
    }

    // Extract the AI-generated response text
    private String extractAiResponse(JsonNode response) {
        log.info("Extracting AI response text from JSON");
        try {
            String text = response.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
            log.debug("Extracted text: {}", text);
            return text;
        } catch (Exception e) {
            log.error("Error extracting AI response: {}", e.getMessage(), e);
            // Return empty string to avoid null pointer exceptions
            return "";
        }
    }

    // Upload two files and return a map with keys "before" and "after"
    private Map<String, String> uploadMultipleFiles(MultipartFile before, MultipartFile after) throws Exception {
        log.info("Uploading multiple files (before and after)");
        Map<String, String> fileUris = new HashMap<>();

        try {
            log.debug("Uploading 'before' image...");
            fileUris.put("before", uploadFileToGoogle(before));
            log.debug("Uploading 'after' image...");
            fileUris.put("after", uploadFileToGoogle(after));
            log.info("Successfully uploaded both files");
            return fileUris;
        } catch (Exception e) {
            log.error("Error uploading multiple files: {}", e.getMessage(), e);
            throw new Exception("File upload failed: " + e.getMessage());
        }
    }

    // Upload a single file and return its URI
    private String uploadFileToGoogle(MultipartFile file) throws Exception {
        log.info("Uploading file: {}, size: {}", file.getOriginalFilename(), file.getSize());
        String uploadUrl = String.format("%s/upload/v1beta/files?key=%s", baseUrl, apiKey);
        log.debug("Upload URL: {}", uploadUrl);

        // Start upload session
        log.debug("Starting upload session");
        HttpHeaders startHeaders = new HttpHeaders();
        startHeaders.set("X-Goog-Upload-Protocol", "resumable");
        startHeaders.set("X-Goog-Upload-Command", "start");
        startHeaders.set("X-Goog-Upload-Header-Content-Length", String.valueOf(file.getSize()));
        startHeaders.set("X-Goog-Upload-Header-Content-Type", file.getContentType());
        startHeaders.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = String.format("{\"file\": {\"display_name\": \"%s\"}}", file.getOriginalFilename());
        HttpEntity<String> startRequest = new HttpEntity<>(jsonBody, startHeaders);

        try {
            ResponseEntity<String> startResponse = restTemplate.exchange(uploadUrl, HttpMethod.POST, startRequest, String.class);
            log.debug("Session start response status: {}", startResponse.getStatusCode());

            String sessionUri = startResponse.getHeaders().getFirst("X-Goog-Upload-URL");
            if (sessionUri == null || sessionUri.isEmpty()) {
                log.error("Failed to obtain upload session URI");
                throw new Exception("Failed to obtain upload session URI.");
            }
            log.debug("Session URI obtained: {}", sessionUri);

            // Upload file
            log.debug("Uploading file data");
            HttpHeaders uploadHeaders = new HttpHeaders();
            uploadHeaders.set("X-Goog-Upload-Protocol", "resumable");
            uploadHeaders.set("X-Goog-Upload-Command", "upload, finalize");
            uploadHeaders.set("X-Goog-Upload-Offset", "0");
            uploadHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            HttpEntity<byte[]> uploadRequest = new HttpEntity<>(file.getBytes(), uploadHeaders);
            ResponseEntity<String> uploadResponse = restTemplate.exchange(sessionUri, HttpMethod.POST, uploadRequest, String.class);
            log.debug("Upload response status: {}", uploadResponse.getStatusCode());

            if (uploadResponse.getBody() == null) {
                log.error("Empty response from Google API during file upload");
                throw new Exception("Empty response from Google API during file upload.");
            }

            String fileUri = objectMapper.readTree(uploadResponse.getBody()).path("file").path("uri").asText();
            log.info("File successfully uploaded, URI: {}", fileUri);
            return fileUri;
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new Exception("File upload failed: " + e.getMessage());
        }
    }

    // ------------------------------
    // XP update method: Only update the user XP.
    // A database trigger will automatically update the team XP.
    // ------------------------------
    private XpAwardResponse addXpToUser(UUID userId, int xp, String activityType) {
        log.info("Starting XP update for user: {}, amount: {}", userId, xp);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            int previousUserXp = user.getXp();
            int newUserXp = previousUserXp + xp;
            user.setXp(newUserXp);
            userRepository.save(user);

            Team team = user.getTeams().isEmpty() ? null : user.getTeams().get(0);
            int previousTeamXp = (team != null) ? team.getXp() : 0;
            int newTeamXp = previousTeamXp + xp;
            if (team != null) {
                team.setXp(newTeamXp);
            }

            UserActivity activity = UserActivity.builder()
                    .user(user)
                    .team(team)
                    .activityType(activityType)
                    .activityXp(xp)
                    .build();
            entityManager.persist(activity);

            return XpAwardResponse.builder()
                    .status("success")
                    .message("XP awarded successfully!")
                    .activity(new XpAwardResponse.ActivityInfo(activityType, activity.getId()))
                    .user(new XpAwardResponse.UserXpInfo(user.getId(), previousUserXp, newUserXp))
                    .team(team != null ? new XpAwardResponse.TeamXpInfo(team.getId(), previousTeamXp, newTeamXp) : null)
                    .build();
        } catch (Exception e) {
            log.error("Error updating XP for user {}: {}", userId, e.getMessage(), e);
            return XpAwardResponse.builder()
                    .status("failure")
                    .message("Failed to update XP: " + e.getMessage())
                    .build();
        }
    }


}