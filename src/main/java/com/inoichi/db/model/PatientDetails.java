package com.inoichi.db.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "patient_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Optional: you can have additional fields if needed.
    // These fields are stored in the User entity already, but you can duplicate some if necessary.
    private String name;
    private int age;
    private String gender;

    private double weight;
    private double height;
    private double bmi;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    @Column(name = "photo_id")
    private String photoId;

    @Column(name = "chronic_diseases")
    private String chronicDiseases;

    private String address;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "emergency_contact_details")
    private String emergencyContactDetails;
}
