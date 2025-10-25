package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Claim;
import com.hims.healthinsurancesystem.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    public Claim save(Claim claim) {
        return claimRepository.save(claim);
    }

    public Optional<Claim> findById(Long id) {
        return claimRepository.findById(id);
    }

    public List<Claim> findAll() {
        return claimRepository.findAll();
    }

    public void deleteById(Long id) {
        claimRepository.deleteById(id);
    }

    // Find claims for a given customer id
    public List<Claim> findByCustomerId(Long customerId) {
        return claimRepository.findByCustomer_Id(customerId);
    }
}