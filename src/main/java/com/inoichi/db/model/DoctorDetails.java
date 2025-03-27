package com.inoichi.db.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "doctor_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String specialization;

    @Column(name = "years_of_experience")
    private int yearsOfExperience;

    private String organization;

    @Column(columnDefinition = "TEXT")
    private String about;
}
