package com.inoichi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GeminiApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.api.base_url}")
    private String baseUrl;

    // Autowire RestTemplate
    public GeminiApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // 🔹 Compare Before & After Images
    public String checkLitterService(MultipartFile beforeImage, MultipartFile afterImage) throws Exception {
        Map<String, String> uploadedFiles = uploadMultipleFiles(beforeImage, afterImage);
        String beforeUri = uploadedFiles.get("before");
        String afterUri = uploadedFiles.get("after");

        String prompt = "Compare the two images. The first image shows an area with litter, and the second image shows the same area.Please analyze the second image and confirm if the litter has been cleaned up and if the area looks better and more pristine.Indicate whether the second image corresponds to the first image in terms of showing a cleaned area and not just an unrelated image. Answer with 'yes' if the litter has been cleaned and the second image is related, or 'no' if the changes are not significant or if the images are unrelated";

        JsonNode response = generateContent(beforeUri, afterUri, prompt);
        String aiResponse = response.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        log.info("AI Response: {}", aiResponse);

        if (aiResponse.toLowerCase().contains("yes")) {
            return "Litter has been cleaned! 🎉 You earned +200 XP!";
        } else {
            return "No significant changes detected. Clean more and try again!";
        }
    }
    // 🔹 Compare Before & After Images
    public String checkTreePlantation(MultipartFile beforeImage, MultipartFile afterImage) throws Exception {
        Map<String, String> uploadedFiles = uploadMultipleFiles(beforeImage, afterImage);
        String beforeUri = uploadedFiles.get("before");
        String afterUri = uploadedFiles.get("after");

        String prompt = "Compare these two images. Is there a noticeable difference between the first and second images, specifically in terms of tree plantation (e.g., trees planted in the second image)?";

        JsonNode response = generateContent(beforeUri, afterUri, prompt);
        String aiResponse = response.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        log.info("AI Response: {}", aiResponse);

        if (aiResponse.toLowerCase().contains("yes")) {
            return "Tree has been planted! 🎉 You earned +200 XP!";
        } else {
            return "No significant changes detected. Plant more and try again!";
        }
    }
    // 🔹 Compare Before & After Images
    public String checkTicket(MultipartFile beforeImage, MultipartFile afterImage) throws Exception {
        Map<String, String> uploadedFiles = uploadMultipleFiles(beforeImage, afterImage);
        String beforeUri = uploadedFiles.get("before");
        String afterUri = uploadedFiles.get("after");

        String prompt = "Analyze this ticket image and verify:1. Is this a valid public transport ticket (Bus/Train/Metro)?2. Does it have a valid date?3. Is the user contributing to CO₂ reduction by using public transport?";

        JsonNode response = generateContent(beforeUri, afterUri, prompt);
        String aiResponse = response.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        log.info("AI Response: {}", aiResponse);

        if (aiResponse.toLowerCase().contains("yes")) {
            return "Tree has been planted! 🎉 You earned +200 XP!";
        } else {
            return "No significant changes detected. Plant more and try again!";
        }
    }
    // 🔹 Upload TWO Files & Return URIs
    private Map<String, String> uploadMultipleFiles(MultipartFile before, MultipartFile after) throws Exception {
        Map<String, String> fileUris = new HashMap<>();
        fileUris.put("before", uploadFileToGoogle(before));
        fileUris.put("after", uploadFileToGoogle(after));
        return fileUris;
    }

    // 🔹 Upload a Single File to Google
    private String uploadFileToGoogle(MultipartFile file) throws Exception {
        String uploadUrl = String.format("%s/upload/v1beta/files?key=%s", baseUrl, apiKey);

        HttpHeaders startHeaders = new HttpHeaders();
        startHeaders.set("X-Goog-Upload-Protocol", "resumable");
        startHeaders.set("X-Goog-Upload-Command", "start");
        startHeaders.set("X-Goog-Upload-Header-Content-Length", String.valueOf(file.getSize()));
        startHeaders.set("X-Goog-Upload-Header-Content-Type", file.getContentType());
        startHeaders.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = String.format("{\"file\": {\"display_name\": \"%s\"}}", file.getOriginalFilename());

        HttpEntity<String> startRequest = new HttpEntity<>(jsonBody, startHeaders);
        ResponseEntity<String> startResponse = restTemplate.exchange(uploadUrl, HttpMethod.POST, startRequest, String.class);

        String sessionUri = startResponse.getHeaders().getFirst("X-Goog-Upload-URL");
        if (sessionUri == null || sessionUri.isEmpty()) {
            throw new Exception("Failed to obtain upload session URI.");
        }

        HttpHeaders uploadHeaders = new HttpHeaders();
        uploadHeaders.set("X-Goog-Upload-Protocol", "resumable");
        uploadHeaders.set("X-Goog-Upload-Command", "upload, finalize");
        uploadHeaders.set("X-Goog-Upload-Offset", "0");
        uploadHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> uploadRequest = new HttpEntity<>(file.getBytes(), uploadHeaders);
        ResponseEntity<String> uploadResponse = restTemplate.exchange(sessionUri, HttpMethod.POST, uploadRequest, String.class);

        if (uploadResponse.getBody() == null) {
            throw new Exception("Empty response from Google API during file upload.");
        }

        return objectMapper.readTree(uploadResponse.getBody()).path("file").path("uri").asText();
    }

    // 🔹 Generate Content Using Gemini AI
    private JsonNode generateContent(String beforeUri, String afterUri, String prompt) throws Exception {
        String url = String.format("%s/v1beta/models/gemini-1.5-flash:generateContent?key=%s", baseUrl, apiKey);

        String requestBody = String.format(
                "{ \"contents\": [ { \"role\": \"user\", \"parts\": [ " +
                        "{ \"text\": \"%s\" }, " +
                        "{ \"fileData\": { \"fileUri\": \"%s\" } }, " +
                        "{ \"fileData\": { \"fileUri\": \"%s\" } } " +
                        " ] } ] }",
                prompt, beforeUri, afterUri
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getBody() == null) {
            throw new Exception("Empty response from Gemini API.");
        }

        return objectMapper.readTree(response.getBody());
    }
}
