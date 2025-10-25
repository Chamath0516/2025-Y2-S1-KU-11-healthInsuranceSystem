package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.PendingHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingHospitalRepository extends JpaRepository<PendingHospital, Long> {
    Optional<PendingHospital> findByEmail(String email);
    boolean existsByEmail(String email);
}
