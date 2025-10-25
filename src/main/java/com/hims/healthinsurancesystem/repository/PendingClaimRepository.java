package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.PendingClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingClaimRepository extends JpaRepository<PendingClaim, Long> {
    List<PendingClaim> findByCustomer_Id(Long customerId);

    // Add method to fetch pending claims assigned to a provider id
    List<PendingClaim> findByProviderId(Long providerId);
}
