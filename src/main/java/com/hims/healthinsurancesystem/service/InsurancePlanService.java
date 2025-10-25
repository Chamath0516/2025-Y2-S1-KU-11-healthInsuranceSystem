package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.InsurancePlan;
import com.hims.healthinsurancesystem.repository.InsurancePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InsurancePlanService {

    @Autowired
    private InsurancePlanRepository insurancePlanRepository;

    public InsurancePlan save(final InsurancePlan insurancePlan) {
        return insurancePlanRepository.save(insurancePlan);
    }

    public Optional<InsurancePlan> findById(final Long id) {
        return insurancePlanRepository.findById(id);
    }

    public List<InsurancePlan> findAll() {
        return insurancePlanRepository.findAll();
    }

    public void deleteById(final Long id) {
        insurancePlanRepository.deleteById(id);
    }

    public List<InsurancePlan> findAllById(Iterable<Long> ids) {
        List<InsurancePlan> result = new ArrayList<>();
        insurancePlanRepository.findAllById(ids).forEach(result::add);
        return result;
    }
}