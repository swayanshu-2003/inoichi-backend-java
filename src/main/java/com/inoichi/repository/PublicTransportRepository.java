package com.inoichi.repository;

import com.inoichi.db.model.PublicTransport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PublicTransportRepository extends JpaRepository<PublicTransport, UUID> {

}
