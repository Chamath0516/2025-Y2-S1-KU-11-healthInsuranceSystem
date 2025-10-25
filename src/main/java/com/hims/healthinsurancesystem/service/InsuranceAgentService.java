package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.InsuranceAgent;
import com.hims.healthinsurancesystem.repository.InsuranceAgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InsuranceAgentService {

    @Autowired
    private InsuranceAgentRepository insuranceAgentRepository;

    public InsuranceAgent save(InsuranceAgent insuranceAgent) {
        return insuranceAgentRepository.save(insuranceAgent);
    }

    public Optional<InsuranceAgent> findById(Long id) {
        return insuranceAgentRepository.findById(id);
    }

    public List<InsuranceAgent> findAll() {
        return insuranceAgentRepository.findAll();
    }

    public void deleteById(Long id) {
        insuranceAgentRepository.deleteById(id);
    }

    public InsuranceAgent findByUsername(String username) {
        if (username == null) return null;
        return insuranceAgentRepository.findByUsername(username.trim()).orElse(null);
    }
}