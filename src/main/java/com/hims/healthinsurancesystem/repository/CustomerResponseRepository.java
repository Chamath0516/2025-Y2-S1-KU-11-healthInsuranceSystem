package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.CustomerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerResponseRepository extends JpaRepository<CustomerResponse, Long> {
    List<CustomerResponse> findByPendingClaimId(Long pendingClaimId);
}
