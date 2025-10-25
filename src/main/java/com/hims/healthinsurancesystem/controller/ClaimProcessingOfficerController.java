package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.ClaimProcessingOfficerService;
import com.hims.healthinsurancesystem.model.ClaimProcessingOfficer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim-processing-officers")
public class ClaimProcessingOfficerController {

    @Autowired
    private ClaimProcessingOfficerService claimProcessingOfficerService;

    @PostMapping
    public ResponseEntity<ClaimProcessingOfficer> createClaimProcessingOfficer(@Valid @RequestBody ClaimProcessingOfficer officer) {
        ClaimProcessingOfficer savedOfficer = claimProcessingOfficerService.save(officer);
        return ResponseEntity.ok(savedOfficer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimProcessingOfficer> getClaimProcessingOfficerById(@PathVariable Long id) {
        return claimProcessingOfficerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ClaimProcessingOfficer>> getAllClaimProcessingOfficers() {
        List<ClaimProcessingOfficer> officers = claimProcessingOfficerService.findAll();
        return ResponseEntity.ok(officers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaimProcessingOfficer> updateClaimProcessingOfficer(@PathVariable Long id, @Valid @RequestBody ClaimProcessingOfficer officer) {
        if (!claimProcessingOfficerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        officer.setId(id);
        ClaimProcessingOfficer updatedOfficer = claimProcessingOfficerService.save(officer);
        return ResponseEntity.ok(updatedOfficer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaimProcessingOfficer(@PathVariable Long id) {
        if (!claimProcessingOfficerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        claimProcessingOfficerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}