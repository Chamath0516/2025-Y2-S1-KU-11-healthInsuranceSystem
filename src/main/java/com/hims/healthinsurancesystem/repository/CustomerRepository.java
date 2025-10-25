package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by username (case-insensitive).
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.username) = LOWER(:username)")
    Optional<Customer> findByUsername(@Param("username") String username);

    /**
     * Check if username exists (case-insensitive).
     */
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(c.username) = LOWER(:username)")
    boolean existsByUsername(@Param("username") String username);

    /**
     * Check if email exists (case-insensitive).
     */
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(c.email) = LOWER(:email)")
    boolean existsByEmail(@Param("email") String email);

    /**
     * Find customer by NIC.
     */
    @Query("SELECT c FROM Customer c WHERE c.nic = :nic")
    Optional<Customer> findByNic(@Param("nic") String nic);

    /**
     * Check if NIC exists.
     */
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.nic = :nic")
    boolean existsByNic(@Param("nic") String nic);
}
