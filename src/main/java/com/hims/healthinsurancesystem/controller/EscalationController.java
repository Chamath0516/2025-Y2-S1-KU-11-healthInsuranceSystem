package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.EscalationService;
import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.model.Escalation;
import com.hims.healthinsurancesystem.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/escalations")
public class EscalationController {

    @Autowired
    private EscalationService escalationService;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<Escalation> createEscalation(@Valid @RequestBody Escalation escalation) {
        if (escalation.getCustomer() == null || escalation.getCustomer().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Long customerId = escalation.getCustomer().getId();
        Optional<Customer> opt = customerRepository.findById(customerId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        escalation.setCustomer(opt.get());
        if (escalation.getStatus() == null) {
            escalation.setStatus("OPEN");
        }
        if (escalation.getCreatedAt() == null) {
            escalation.setCreatedAt(LocalDateTime.now());
        }
        Escalation saved = escalationService.save(escalation);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Escalation> updateEscalationStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        if (payload == null || !payload.containsKey("status")) {
            return ResponseEntity.badRequest().build();
        }
        String status = payload.get("status");
        Optional<Escalation> opt = escalationService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Escalation esc = opt.get();
        esc.setStatus(status);
        Escalation saved = escalationService.save(esc);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<Escalation> assignEscalation(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        if (payload == null || !payload.containsKey("assignedToRole")) {
            return ResponseEntity.badRequest().build();
        }
        String assignedToRole = payload.get("assignedToRole");
        Optional<Escalation> opt = escalationService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Escalation esc = opt.get();
        esc.setAssignedToRole(assignedToRole);
        Escalation saved = escalationService.save(esc);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Escalation> getById(@PathVariable Long id) {
        return escalationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Escalation>> getAll() {
        return ResponseEntity.ok(escalationService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Escalation> update(@PathVariable Long id, @Valid @RequestBody Escalation escalation) {
        if (escalationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        escalation.setId(id);
        return ResponseEntity.ok(escalationService.save(escalation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (escalationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        escalationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
