package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.HospitalService;
import com.hims.healthinsurancesystem.model.Hospital;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<Hospital> createHospital(@Valid @RequestBody Hospital hospital) {
        Hospital savedHospital = hospitalService.save(hospital);
        return ResponseEntity.ok(savedHospital);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospitalById(@PathVariable Long id) {
        return hospitalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        List<Hospital> hospitals = hospitalService.findAll();
        return ResponseEntity.ok(hospitals);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hospital> updateHospital(@PathVariable Long id, @Valid @RequestBody Hospital hospital) {
        if (!hospitalService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        hospital.setId(id);
        Hospital updatedHospital = hospitalService.save(hospital);
        return ResponseEntity.ok(updatedHospital);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        if (!hospitalService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        hospitalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}