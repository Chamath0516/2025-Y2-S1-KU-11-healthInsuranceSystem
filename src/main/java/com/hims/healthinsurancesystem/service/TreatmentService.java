package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Treatment;
import com.hims.healthinsurancesystem.repository.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TreatmentService {

    @Autowired
    private TreatmentRepository treatmentRepository;

    public Treatment save(Treatment treatment) {
        return treatmentRepository.save(treatment);
    }

    public Optional<Treatment> findById(Long id) {
        return treatmentRepository.findById(id);
    }

    public List<Treatment> findAll() {
        return treatmentRepository.findAll();
    }

    public void deleteById(Long id) {
        treatmentRepository.deleteById(id);
    }
}