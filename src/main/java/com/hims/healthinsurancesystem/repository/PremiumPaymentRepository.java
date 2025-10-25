package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.PremiumPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PremiumPaymentRepository extends JpaRepository<PremiumPayment, Long> {
    List<PremiumPayment> findByCustomer_Id(Long customerId);
    List<PremiumPayment> findByCustomer_IdAndMonthAndInsurancePlan_Id(Long customerId, String month, Long insurancePlanId);
}
