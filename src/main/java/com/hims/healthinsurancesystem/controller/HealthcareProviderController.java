package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.HealthcareProviderService;
import com.hims.healthinsurancesystem.model.HealthcareProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/healthcare-providers")
public class HealthcareProviderController {

    @Autowired
    private HealthcareProviderService healthcareProviderService;

    @PostMapping
    public ResponseEntity<HealthcareProvider> createHealthcareProvider(@Valid @RequestBody HealthcareProvider provider) {
        HealthcareProvider savedProvider = healthcareProviderService.save(provider);
        return ResponseEntity.ok(savedProvider);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthcareProvider> getHealthcareProviderById(@PathVariable Long id) {
        return healthcareProviderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<HealthcareProvider>> getAllHealthcareProviders() {
        List<HealthcareProvider> providers = healthcareProviderService.findAll();
        return ResponseEntity.ok(providers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthcareProvider> updateHealthcareProvider(@PathVariable Long id, @Valid @RequestBody HealthcareProvider provider) {
        if (!healthcareProviderService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        provider.setId(id);
        HealthcareProvider updatedProvider = healthcareProviderService.save(provider);
        return ResponseEntity.ok(updatedProvider);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthcareProvider(@PathVariable Long id) {
        if (!healthcareProviderService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        healthcareProviderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}