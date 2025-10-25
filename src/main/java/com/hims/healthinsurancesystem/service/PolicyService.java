package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Policy;
import com.hims.healthinsurancesystem.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    public Policy save(Policy policy) {
        return policyRepository.save(policy);
    }

    public Optional<Policy> findById(Long id) {
        return policyRepository.findById(id);
    }

    public List<Policy> findAll() {
        return policyRepository.findAll();
    }

    public List<Policy> findByStatus(String status) {
        return policyRepository.findByStatus(status);
    }

    public void deleteById(Long id) {
        policyRepository.deleteById(id);
    }

    // New helper: find policies for a specific customer id
    public List<Policy> findByCustomerId(Long customerId) {
        return policyRepository.findByCustomer_Id(customerId);
    }
}