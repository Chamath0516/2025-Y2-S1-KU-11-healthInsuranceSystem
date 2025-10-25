package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.InsuranceAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsuranceAgentRepository extends JpaRepository<InsuranceAgent, Long> {
    // Add custom queries if needed
    Optional<InsuranceAgent> findByUsername(String username);
}