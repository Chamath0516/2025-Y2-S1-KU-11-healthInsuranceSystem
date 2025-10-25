package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.PendingHospital;
import com.hims.healthinsurancesystem.repository.PendingHospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PendingHospitalService {

    @Autowired
    private PendingHospitalRepository pendingHospitalRepository;

    @Transactional
    public PendingHospital save(PendingHospital pendingHospital) {
        return pendingHospitalRepository.save(pendingHospital);
    }

    public Optional<PendingHospital> findById(Long id) {
        return pendingHospitalRepository.findById(id);
    }

    public List<PendingHospital> findAll() {
        return pendingHospitalRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        pendingHospitalRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return pendingHospitalRepository.existsByEmail(email.trim().toLowerCase());
    }

    public Optional<PendingHospital> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return pendingHospitalRepository.findByEmail(email.trim().toLowerCase());
    }
}
