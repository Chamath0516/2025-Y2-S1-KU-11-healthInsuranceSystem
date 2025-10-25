package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {
    // Add custom queries if needed
}