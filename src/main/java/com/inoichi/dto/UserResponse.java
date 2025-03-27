package com.inoichi.dto;

import com.inoichi.db.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String name;
    private int age;
    private String gender;
    private String address;
    private String role;

    //  Fix: Add this constructor to accept only a User object
    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.age = user.getAge();
        this.gender = user.getGender();
        this.address = user.getAddress();
        this.role = user.getRole();
    }
}
