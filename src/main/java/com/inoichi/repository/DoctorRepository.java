package com.inoichi.repository;

import com.inoichi.db.model.DoctorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorDetails, UUID> {
}
