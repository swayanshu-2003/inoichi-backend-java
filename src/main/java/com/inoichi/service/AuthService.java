package com.inoichi.service;

import com.inoichi.db.model.User;
import com.inoichi.db.model.DoctorDetails;
import com.inoichi.db.model.PatientDetails;
import com.inoichi.dto.DoctorSignupRequest;
import com.inoichi.dto.PatientSignupRequest;
import com.inoichi.dto.UserResponse;
import com.inoichi.repository.UserRepository;
import com.inoichi.repository.DoctorRepository;
import com.inoichi.repository.PatientRepository;
import com.inoichi.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User registerDoctor(DoctorSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("DOCTOR");
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());

        user = userRepository.save(user);

        DoctorDetails doctorDetails = new DoctorDetails();
        doctorDetails.setUser(user);
        doctorDetails.setSpecialization(request.getSpecialization());
        doctorDetails.setYearsOfExperience(request.getYearsOfExperience());
        doctorDetails.setOrganization(request.getOrganization());
        doctorDetails.setAbout(request.getAbout());

        doctorRepository.save(doctorDetails);

        return user;
    }

    public User registerPatient(PatientSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("PATIENT");
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());

        user = userRepository.save(user);

        PatientDetails patientDetails = new PatientDetails();
        patientDetails.setUser(user);
        patientDetails.setWeight(request.getWeight());
        patientDetails.setHeight(request.getHeight());
        patientDetails.setBmi(request.getBmi());
        patientDetails.setPreferredLanguage(request.getPreferredLanguage());
        patientDetails.setChronicDiseases(request.getChronicDiseases());
        patientDetails.setBloodGroup(request.getBloodGroup());
        patientDetails.setEmergencyContactDetails(request.getEmergencyContactDetails());

        patientRepository.save(patientDetails);

        return user;
    }

    public String authenticateAndGenerateToken(String email, String password) {
        authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(email, password)
        );
        return jwtUtil.generateToken(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
