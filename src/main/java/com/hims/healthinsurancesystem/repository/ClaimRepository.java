package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    // Find claims belonging to a specific customer
    List<Claim> findByCustomer_Id(Long customerId);
}