package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.DeniedRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeniedRefundRepository extends JpaRepository<DeniedRefund, Long> {
}
