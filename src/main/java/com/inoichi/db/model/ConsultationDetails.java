package com.inoichi.db.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consultations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorDetails doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientDetails patient;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;
}
