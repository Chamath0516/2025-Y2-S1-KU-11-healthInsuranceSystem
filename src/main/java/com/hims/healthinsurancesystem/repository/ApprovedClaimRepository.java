package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.ApprovedClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovedClaimRepository extends JpaRepository<ApprovedClaim, Long> {
    List<ApprovedClaim> findByApprovedBy(String approvedBy);
}
