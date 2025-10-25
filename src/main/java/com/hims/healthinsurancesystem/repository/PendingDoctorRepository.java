package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.PendingDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingDoctorRepository extends JpaRepository<PendingDoctor, Long> {
    Optional<PendingDoctor> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<PendingDoctor> findByLicenseNumber(String licenseNumber);
    boolean existsByLicenseNumber(String licenseNumber);
    Optional<PendingDoctor> findByUsername(String username);
    boolean existsByUsername(String username);
}
