package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.MedicalReportService;
import com.hims.healthinsurancesystem.model.MedicalReport;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-reports")
public class MedicalReportController {

    @Autowired
    private MedicalReportService medicalReportService;

    @PostMapping
    public ResponseEntity<MedicalReport> createMedicalReport(@Valid @RequestBody MedicalReport report) {
        MedicalReport savedReport = medicalReportService.save(report);
        return ResponseEntity.ok(savedReport);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalReport> getMedicalReportById(@PathVariable Long id) {
        return medicalReportService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<MedicalReport>> getAllMedicalReports() {
        List<MedicalReport> reports = medicalReportService.findAll();
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalReport> updateMedicalReport(@PathVariable Long id, @Valid @RequestBody MedicalReport report) {
        if (!medicalReportService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        report.setId(id);
        MedicalReport updatedReport = medicalReportService.save(report);
        return ResponseEntity.ok(updatedReport);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalReport(@PathVariable Long id) {
        if (!medicalReportService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        medicalReportService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}