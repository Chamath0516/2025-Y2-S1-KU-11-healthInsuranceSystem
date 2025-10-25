package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    /** ✅ Find a hospital by username */
    Optional<Hospital> findByUsername(String username);

    /** ✅ Check if username already exists */
    boolean existsByUsername(String username);

    /** ✅ Check if email already exists */
    boolean existsByEmail(String email);

    /** ✅ Existing custom query (keep this) */
    List<Hospital> findByStatus(Hospital.ApprovalStatus status);

    // Search hospitals by name for autocomplete (case-insensitive, contains)
    List<Hospital> findByNameContainingIgnoreCase(String name);
}
