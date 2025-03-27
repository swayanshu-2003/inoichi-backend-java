package com.inoichi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientSignupRequest {
    private String email;
    private String password;
    private String name;
    private int age;
    private String gender;
    private String address;
    private double weight;
    private double height;
    private double bmi;
    private String preferredLanguage;
    private String chronicDiseases;
    private String bloodGroup;
    private String emergencyContactDetails;
}
