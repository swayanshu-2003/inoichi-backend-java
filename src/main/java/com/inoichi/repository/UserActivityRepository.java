package com.inoichi.repository;

import com.inoichi.db.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserActivityRepository extends JpaRepository<UserActivity, UUID> {
}
