package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.CustomerSupportOfficerService;
import com.hims.healthinsurancesystem.model.CustomerSupportOfficer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-support-officers")
public class CustomerSupportOfficerController {

    @Autowired
    private CustomerSupportOfficerService customerSupportOfficerService;

    @PostMapping
    public ResponseEntity<CustomerSupportOfficer> createCustomerSupportOfficer(@Valid @RequestBody CustomerSupportOfficer officer) {
        CustomerSupportOfficer savedOfficer = customerSupportOfficerService.save(officer);
        return ResponseEntity.ok(savedOfficer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerSupportOfficer> getCustomerSupportOfficerById(@PathVariable Long id) {
        return customerSupportOfficerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CustomerSupportOfficer>> getAllCustomerSupportOfficers() {
        List<CustomerSupportOfficer> officers = customerSupportOfficerService.findAll();
        return ResponseEntity.ok(officers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerSupportOfficer> updateCustomerSupportOfficer(@PathVariable Long id, @Valid @RequestBody CustomerSupportOfficer officer) {
        if (!customerSupportOfficerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        officer.setId(id);
        CustomerSupportOfficer updatedOfficer = customerSupportOfficerService.save(officer);
        return ResponseEntity.ok(updatedOfficer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerSupportOfficer(@PathVariable Long id) {
        if (!customerSupportOfficerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        customerSupportOfficerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}