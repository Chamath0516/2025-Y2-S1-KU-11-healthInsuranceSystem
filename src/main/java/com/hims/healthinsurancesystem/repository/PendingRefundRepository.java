package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.PendingRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingRefundRepository extends JpaRepository<PendingRefund, Long> {
    List<PendingRefund> findByCustomer_Id(Long customerId);
}
