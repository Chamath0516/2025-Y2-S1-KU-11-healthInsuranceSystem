package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Add custom queries if needed
}