package com.inoichi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSignupRequest {
    private String email;
    private String password;
    private String name;
    private int age;
    private String gender;
    private String address;
    private String specialization;
    private int yearsOfExperience;
    private String organization;
    private String about;
}
