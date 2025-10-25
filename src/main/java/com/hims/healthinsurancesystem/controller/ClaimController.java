package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.ClaimService;
import com.hims.healthinsurancesystem.model.Claim;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @PostMapping
    public ResponseEntity<Claim> createClaim(@Valid @RequestBody Claim claim) {
        Claim savedClaim = claimService.save(claim);
        return ResponseEntity.ok(savedClaim);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id) {
        return claimService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        List<Claim> claims = claimService.findAll();
        return ResponseEntity.ok(claims);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Claim> updateClaim(@PathVariable Long id, @Valid @RequestBody Claim claim) {
        if (!claimService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        claim.setId(id);
        Claim updatedClaim = claimService.save(claim);
        return ResponseEntity.ok(updatedClaim);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        if (!claimService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        claimService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}