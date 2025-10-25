package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.FinanceManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinanceManagerRepository extends JpaRepository<FinanceManager, Long> {
    // Add custom queries if needed
}