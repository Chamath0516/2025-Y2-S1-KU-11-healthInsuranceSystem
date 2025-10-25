package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Hospital;
import com.hims.healthinsurancesystem.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Transactional
    public Hospital save(Hospital hospital) {
        return hospitalRepository.save(hospital);
    }

    public Optional<Hospital> findById(Long id) {
        return hospitalRepository.findById(id);
    }

    public List<Hospital> findAll() {
        return hospitalRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        hospitalRepository.deleteById(id);
    }

    /** ✅ Check if username exists */
    public boolean existsByUsername(String username) {
        if (username == null) return false;
        return hospitalRepository.existsByUsername(username.trim());
    }

    /** ✅ Check if email exists */
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return hospitalRepository.existsByEmail(email.trim());
    }

    /** ✅ Find hospital by username */
    public Hospital findByUsername(String username) {
        if (username == null) return null;
        return hospitalRepository.findByUsername(username.trim())
                .orElse(null);
    }

    /** ✅ NEW: Find hospitals by approval status (used by AdminController) */
    public List<Hospital> findByStatus(Hospital.ApprovalStatus status) {
        return hospitalRepository.findByStatus(status);
    }
}
