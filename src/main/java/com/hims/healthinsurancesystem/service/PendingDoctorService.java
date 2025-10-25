package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.PendingDoctor;
import com.hims.healthinsurancesystem.repository.PendingDoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PendingDoctorService {

    @Autowired
    private PendingDoctorRepository pendingDoctorRepository;

    @Transactional
    public PendingDoctor save(PendingDoctor pendingDoctor) {
        return pendingDoctorRepository.save(pendingDoctor);
    }

    public Optional<PendingDoctor> findById(Long id) {
        return pendingDoctorRepository.findById(id);
    }

    public List<PendingDoctor> findAll() {
        return pendingDoctorRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        pendingDoctorRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return pendingDoctorRepository.existsByEmail(email.trim().toLowerCase());
    }

    public boolean existsByLicenseNumber(String licenseNumber) {
        if (licenseNumber == null) return false;
        return pendingDoctorRepository.existsByLicenseNumber(licenseNumber.trim());
    }

    public Optional<PendingDoctor> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return pendingDoctorRepository.findByEmail(email.trim().toLowerCase());
    }

    public boolean existsByUsername(String username) {
        if (username == null) return false;
        return pendingDoctorRepository.existsByUsername(username.trim());
    }

    public Optional<PendingDoctor> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return pendingDoctorRepository.findByUsername(username.trim());
    }

    // Find pending doctor by license number (trimmed)
    public Optional<PendingDoctor> findByLicenseNumber(String licenseNumber) {
        if (licenseNumber == null) return Optional.empty();
        return pendingDoctorRepository.findByLicenseNumber(licenseNumber.trim());
    }
}
