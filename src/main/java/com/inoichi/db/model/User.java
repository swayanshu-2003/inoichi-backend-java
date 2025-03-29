package com.inoichi.db.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", schema = "app")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    private String geolocation;  // Store latitude and longitude as a string (e.g., "37.7749,-122.4194")

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserTeam> userTeams;

    @ManyToMany(mappedBy = "members")
    private Set<Team> teams;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int xp; // Ensure XP is initialized to 0

    public List<Team> getTeams() {
        return userTeams.stream()
                .map(UserTeam::getTeam)
                .collect(Collectors.toList());
    }
}
