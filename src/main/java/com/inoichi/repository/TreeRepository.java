package com.inoichi.repository;

import com.inoichi.db.model.Tree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TreeRepository extends JpaRepository<Tree, UUID> {
}
