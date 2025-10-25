package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.HealthcareProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthcareProviderRepository extends JpaRepository<HealthcareProvider, Long> {

    /** ✅ Find a provider by username */
    Optional<HealthcareProvider> findByUsername(String username);

    /** ✅ Check if username already exists */
    boolean existsByUsername(String username);

    /** ✅ Check if email already exists */
    boolean existsByEmail(String email);

    /** ✅ Check/find by license number to support pending doctor approval */
    Optional<HealthcareProvider> findByLicenseNumber(String licenseNumber);
    boolean existsByLicenseNumber(String licenseNumber);

    // Find all providers for a hospital
    List<HealthcareProvider> findByHospitalId(Long hospitalId);

    // Search providers (doctors) by hospital and query (matches first name, last name or specialization)
    @org.springframework.data.jpa.repository.Query("SELECT p FROM HealthcareProvider p WHERE p.hospital.id = :hospitalId AND (LOWER(p.firstName) LIKE CONCAT('%', LOWER(:q), '%') OR LOWER(p.lastName) LIKE CONCAT('%', LOWER(:q), '%') OR LOWER(p.specialization) LIKE CONCAT('%', LOWER(:q), '%'))")
    List<HealthcareProvider> searchByHospitalAndQuery(@org.springframework.data.repository.query.Param("hospitalId") Long hospitalId, @org.springframework.data.repository.query.Param("q") String q);
}
