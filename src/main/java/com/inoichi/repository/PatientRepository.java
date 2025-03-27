package com.inoichi.repository;

import com.inoichi.db.model.PatientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<PatientDetails, UUID> {
}
