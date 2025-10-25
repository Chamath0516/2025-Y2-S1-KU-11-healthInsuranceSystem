package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.ToClaimOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToClaimOfficerRepository extends JpaRepository<ToClaimOfficer, Long> {
    List<ToClaimOfficer> findByProviderId(Long providerId);
    List<ToClaimOfficer> findByPendingClaimId(Long pendingClaimId);
}
