package com.inoichi.repository;

import com.inoichi.db.model.Team;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByHouseId(UUID houseId);
    @Modifying
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("UPDATE Team t SET t.xp = t.xp + :xp WHERE t.id = :teamId")
    void addXp(@Param("teamId") UUID teamId, @Param("xp") int xp);

    @Query(value = "SELECT xp FROM app.teams WHERE id = :teamId", nativeQuery = true)
    int getTeamXp(@Param("teamId") UUID teamId);
    @Query("SELECT t FROM Team t ORDER BY t.xp DESC")
    List<Team> findTop20TeamsByXp(Pageable pageable);
}
