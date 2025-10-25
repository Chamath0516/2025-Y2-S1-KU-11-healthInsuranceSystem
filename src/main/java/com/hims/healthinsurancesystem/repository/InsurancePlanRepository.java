package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.InsurancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, Long> {
    // Add custom queries if needed
    Optional<InsurancePlan> findByName(String name);
}