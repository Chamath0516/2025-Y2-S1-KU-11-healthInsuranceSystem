package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.CustomerSupportOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerSupportOfficerRepository extends JpaRepository<CustomerSupportOfficer, Long> {
    // Add custom queries if needed
}