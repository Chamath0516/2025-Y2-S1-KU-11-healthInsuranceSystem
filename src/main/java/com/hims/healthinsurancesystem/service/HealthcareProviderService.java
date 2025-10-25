package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.HealthcareProvider;
import com.hims.healthinsurancesystem.repository.HealthcareProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HealthcareProviderService {

    @Autowired
    private HealthcareProviderRepository healthcareProviderRepository;

    @Transactional
    public HealthcareProvider save(HealthcareProvider provider) {
        return healthcareProviderRepository.save(provider);
    }

    public Optional<HealthcareProvider> findById(Long id) {
        return healthcareProviderRepository.findById(id);
    }

    public List<HealthcareProvider> findAll() {
        return healthcareProviderRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        healthcareProviderRepository.deleteById(id);
    }

    /** ✅ Add these helper methods (same as in CustomerService) */
    public boolean existsByUsername(String username) {
        if (username == null) return false;
        return healthcareProviderRepository.existsByUsername(username.trim());
    }

    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return healthcareProviderRepository.existsByEmail(email.trim());
    }

    public HealthcareProvider findByUsername(String username) {
        if (username == null) return null;
        return healthcareProviderRepository.findByUsername(username.trim())
                .orElse(null);
    }

    // Check/find by license number
    public boolean existsByLicenseNumber(String licenseNumber) {
        if (licenseNumber == null) return false;
        return healthcareProviderRepository.existsByLicenseNumber(licenseNumber.trim());
    }

    public Optional<HealthcareProvider> findByLicenseNumber(String licenseNumber) {
        if (licenseNumber == null) return Optional.empty();
        return healthcareProviderRepository.findByLicenseNumber(licenseNumber.trim());
    }

}
