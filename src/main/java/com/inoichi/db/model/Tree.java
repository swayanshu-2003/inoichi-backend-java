package com.inoichi.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tree {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String location; // Store latitude and longitude as a string (e.g., "37.7749,-122.4194")

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
