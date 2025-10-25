package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.ClaimProcessingOfficer;
import com.hims.healthinsurancesystem.repository.ClaimProcessingOfficerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimProcessingOfficerService {

    @Autowired
    private ClaimProcessingOfficerRepository claimProcessingOfficerRepository;

    public ClaimProcessingOfficer save(ClaimProcessingOfficer officer) {
        return claimProcessingOfficerRepository.save(officer);
    }

    public Optional<ClaimProcessingOfficer> findById(Long id) {
        return claimProcessingOfficerRepository.findById(id);
    }

    public List<ClaimProcessingOfficer> findAll() {
        return claimProcessingOfficerRepository.findAll();
    }

    public void deleteById(Long id) {
        claimProcessingOfficerRepository.deleteById(id);
    }
}