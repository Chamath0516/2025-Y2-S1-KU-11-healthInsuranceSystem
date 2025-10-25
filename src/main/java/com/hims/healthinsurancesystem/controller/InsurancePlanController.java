package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.InsurancePlanService;
import com.hims.healthinsurancesystem.model.InsurancePlan;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance-plans")
public class InsurancePlanController {

    @Autowired
    private InsurancePlanService insurancePlanService;

    @PostMapping
    public ResponseEntity<InsurancePlan> createInsurancePlan(@Valid @RequestBody InsurancePlan plan) {
        InsurancePlan savedPlan = insurancePlanService.save(plan);
        return ResponseEntity.ok(savedPlan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsurancePlan> getInsurancePlanById(@PathVariable Long id) {
        return insurancePlanService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<InsurancePlan>> getAllInsurancePlans() {
        List<InsurancePlan> plans = insurancePlanService.findAll();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/compare")
    public ResponseEntity<List<InsurancePlan>> compareInsurancePlans(@RequestParam("ids") List<Long> ids) {
        List<InsurancePlan> plans = insurancePlanService.findAllById(ids);
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsurancePlan> updateInsurancePlan(@PathVariable Long id, @Valid @RequestBody InsurancePlan plan) {
        if (insurancePlanService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        plan.setId(id);
        InsurancePlan updatedPlan = insurancePlanService.save(plan);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsurancePlan(@PathVariable Long id) {
        if (insurancePlanService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        insurancePlanService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}