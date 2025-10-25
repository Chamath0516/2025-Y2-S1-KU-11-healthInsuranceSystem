package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.TreatmentService;
import com.hims.healthinsurancesystem.model.Treatment;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {

    @Autowired
    private TreatmentService treatmentService;

    @PostMapping
    public ResponseEntity<Treatment> createTreatment(@Valid @RequestBody Treatment treatment) {
        Treatment savedTreatment = treatmentService.save(treatment);
        return ResponseEntity.ok(savedTreatment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Treatment> getTreatmentById(@PathVariable Long id) {
        return treatmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Treatment>> getAllTreatments() {
        List<Treatment> treatments = treatmentService.findAll();
        return ResponseEntity.ok(treatments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Treatment> updateTreatment(@PathVariable Long id, @Valid @RequestBody Treatment treatment) {
        if (!treatmentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        treatment.setId(id);
        Treatment updatedTreatment = treatmentService.save(treatment);
        return ResponseEntity.ok(updatedTreatment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTreatment(@PathVariable Long id) {
        if (!treatmentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        treatmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}