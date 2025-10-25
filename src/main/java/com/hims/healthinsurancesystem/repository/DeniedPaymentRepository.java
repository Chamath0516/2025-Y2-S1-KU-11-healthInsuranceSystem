package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.DeniedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeniedPaymentRepository extends JpaRepository<DeniedPayment, Long> {
}
