package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.MedicalReport;
import com.hims.healthinsurancesystem.repository.MedicalReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalReportService {

    @Autowired
    private MedicalReportRepository medicalReportRepository;

    public MedicalReport save(MedicalReport report) {
        return medicalReportRepository.save(report);
    }

    public Optional<MedicalReport> findById(Long id) {
        return medicalReportRepository.findById(id);
    }

    public List<MedicalReport> findAll() {
        return medicalReportRepository.findAll();
    }

    public void deleteById(Long id) {
        medicalReportRepository.deleteById(id);
    }
}