package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByStatus(String status);

    // Find policies for a given customer id
    List<Policy> findByCustomer_Id(Long customerId);
}