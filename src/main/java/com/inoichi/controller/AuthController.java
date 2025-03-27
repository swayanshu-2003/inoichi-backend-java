package com.inoichi.controller;

import com.inoichi.db.model.User;
import com.inoichi.dto.AuthRequest;
import com.inoichi.dto.DoctorSignupRequest;
import com.inoichi.dto.PatientSignupRequest;
import com.inoichi.dto.UserResponse;
import com.inoichi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup/doctor")
    public ResponseEntity<UserResponse> registerDoctor(@RequestBody DoctorSignupRequest request) {
        User doctor = authService.registerDoctor(request);
        String token = authService.authenticateAndGenerateToken(doctor.getEmail(), request.getPassword());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token) // Token in header
                .body(new UserResponse(doctor));
    }

    @PostMapping("/signup/patient")
    public ResponseEntity<UserResponse> registerPatient(@RequestBody PatientSignupRequest request) {
        User patient = authService.registerPatient(request);
        String token = authService.authenticateAndGenerateToken(patient.getEmail(), request.getPassword());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token) // Token in header
                .body(new UserResponse(patient));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> authenticateUser(@RequestBody AuthRequest request) {
        String token = authService.authenticateAndGenerateToken(request.getEmail(), request.getPassword());
        User user = authService.getUserByEmail(request.getEmail());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token) // Token in header
                .body(new UserResponse(user));
    }

}
