package com.inoichi.repository;

import com.inoichi.db.model.UserQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserQueryRepository extends JpaRepository<UserQuery, UUID> {
    List<UserQuery> findByUserId(UUID userId);
}
