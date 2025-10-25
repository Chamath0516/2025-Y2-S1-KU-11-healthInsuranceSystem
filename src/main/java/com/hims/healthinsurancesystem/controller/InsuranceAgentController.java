package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.InsuranceAgentService;
import com.hims.healthinsurancesystem.model.InsuranceAgent;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance-agents")
public class InsuranceAgentController {

    @Autowired
    private InsuranceAgentService insuranceAgentService;

    @PostMapping
    public ResponseEntity<InsuranceAgent> createInsuranceAgent(@Valid @RequestBody InsuranceAgent agent) {
        InsuranceAgent savedAgent = insuranceAgentService.save(agent);
        return ResponseEntity.ok(savedAgent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceAgent> getInsuranceAgentById(@PathVariable Long id) {
        return insuranceAgentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<InsuranceAgent>> getAllInsuranceAgents() {
        List<InsuranceAgent> agents = insuranceAgentService.findAll();
        return ResponseEntity.ok(agents);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsuranceAgent> updateInsuranceAgent(@PathVariable Long id, @Valid @RequestBody InsuranceAgent agent) {
        if (!insuranceAgentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        agent.setId(id);
        InsuranceAgent updatedAgent = insuranceAgentService.save(agent);
        return ResponseEntity.ok(updatedAgent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsuranceAgent(@PathVariable Long id) {
        if (!insuranceAgentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        insuranceAgentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}