package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.ClaimProcessingOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimProcessingOfficerRepository extends JpaRepository<ClaimProcessingOfficer, Long> {
    // Add custom queries if needed
}