package com.inoichi.repository;

import com.inoichi.db.model.User;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @EntityGraph(attributePaths = {"userTeams.team"})
    Optional<User> findById(UUID id);
    @Modifying
    @Transactional
    @Query(value = "UPDATE app.users SET xp = xp + :xp WHERE id = :userId", nativeQuery = true)
    void addXp(@Param("userId") UUID userId, @Param("xp") int xp);
    @Query(value = "SELECT xp FROM app.users WHERE id = :userId", nativeQuery = true)
    int getUserXp(@Param("userId") UUID userId);
    @Query("SELECT u FROM User u ORDER BY u.xp DESC")
    List<User> findTop20UsersByXp(Pageable pageable);

}
