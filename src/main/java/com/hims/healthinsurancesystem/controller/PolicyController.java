package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.*;
import com.hims.healthinsurancesystem.dto.ApplyPolicyRequest;
import com.hims.healthinsurancesystem.dto.ApprovePolicyRequest;
import com.hims.healthinsurancesystem.dto.PolicyResponse;
import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.model.InsuranceAgent;
import com.hims.healthinsurancesystem.model.InsurancePlan;
import com.hims.healthinsurancesystem.model.Policy;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private static final Logger log = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    private PolicyService policyService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private InsurancePlanService insurancePlanService;
    @Autowired
    private PlanEligibilityService planEligibilityService;
    @Autowired
    private InsuranceAgentService insuranceAgentService;

    @PostMapping
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody Policy policy) {
        Policy savedPolicy = policyService.save(policy);
        return ResponseEntity.ok(savedPolicy);
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyForPolicy(@RequestBody ApplyPolicyRequest request) {
        log.debug("Apply request: planId={}", request != null ? request.getPlanId() : null);
        if (request == null || request.getPlanId() == null) {
            return ResponseEntity.badRequest().body("planId is required");
        }
        try {
            // Derive customer from authenticated principal
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String login = auth != null ? auth.getName() : null;
            Customer customer = null;
            if (login != null) {
                customer = customerService.findByNic(login);
                if (customer == null) customer = customerService.findByUsername(login);
            }
            if (customer == null) {
                return ResponseEntity.badRequest().body("Logged-in user is not a customer or customer record not found");
            }

            Optional<InsurancePlan> planOpt = insurancePlanService.findById(request.getPlanId());
            log.debug("Resolved plan present? {}", planOpt.isPresent());
            InsurancePlan plan = planOpt.orElse(null);

            PlanEligibilityService.ValidationResult result = planEligibilityService.validate(customer, plan);
            if (!result.isEligible()) {
                log.debug("Eligibility failed: {}", result.getReasons());
                return ResponseEntity.badRequest().body(result.getReasons());
            }

            Policy policy = new Policy();
            policy.setCustomer(customer);
            policy.setInsurancePlan(plan);
            LocalDate start = LocalDate.now();
            policy.setStartDate(start);
            policy.setEndDate(start.plusYears(1));
            policy.setStatus("PENDING_APPROVAL");
            policy.setPolicyNumber(generatePolicyNumber(plan));

            Policy saved = policyService.save(policy);
            log.debug("Policy saved: id={}, number={}", saved.getId(), saved.getPolicyNumber());
            return ResponseEntity.ok(toResponse(saved));
        } catch (DataAccessException ex) {
            log.error("DB error in apply flow", ex);
            return ResponseEntity.internalServerError().body("Database error: " + ex.getMostSpecificCause().getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error in apply flow", ex);
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }

    private String generatePolicyNumber(InsurancePlan plan) {
        String prefix = plan != null && plan.getName() != null ? plan.getName().replaceAll("[^A-Za-z]", "").toUpperCase() : "PLAN";
        prefix = prefix.length() > 6 ? prefix.substring(0, 6) : prefix;
        return prefix + "-" + System.currentTimeMillis();
    }

    private PolicyResponse toResponse(Policy p) {
        String planName = p.getInsurancePlan() != null ? p.getInsurancePlan().getName() : null;
        String custName = p.getCustomer() != null ? (p.getCustomer().getFirstName() + " " + p.getCustomer().getLastName()) : null;
        return new PolicyResponse(p.getId(), p.getPolicyNumber(), p.getStatus(), planName, custName);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approvePolicy(@PathVariable Long id, @RequestBody ApprovePolicyRequest req) {
        Optional<Policy> polOpt = policyService.findById(id);
        if (polOpt.isEmpty()) return ResponseEntity.notFound().build();
        Policy policy = polOpt.get();

        if (req == null || req.getStartDate() == null || req.getEndDate() == null) {
            return ResponseEntity.badRequest().body("startDate and endDate are required");
        }
        if (!req.getEndDate().isAfter(req.getStartDate())) {
            return ResponseEntity.badRequest().body("End date must be at least one day after start date");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        InsuranceAgent agent = auth != null ? insuranceAgentService.findByUsername(auth.getName()) : null;

        policy.setStartDate(req.getStartDate());
        policy.setEndDate(req.getEndDate());
        policy.setStatus("APPROVED");
        policy.setApprovedByAgent(agent);
        Policy saved = policyService.save(policy);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectPolicy(@PathVariable Long id) {
        Optional<Policy> polOpt = policyService.findById(id);
        if (polOpt.isEmpty()) return ResponseEntity.notFound().build();
        Policy policy = polOpt.get();

        if (policy.getStatus() == null || !policy.getStatus().equals("PENDING_APPROVAL")) {
            return ResponseEntity.badRequest().body("Only policies with status PENDING_APPROVAL can be rejected");
        }

        policy.setStatus("REJECTED");
        Policy saved = policyService.save(policy);
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<Policy>> getPolicies(@RequestParam(value = "status", required = false) String status) {
        List<Policy> policies = (status == null || status.isBlank())
                ? policyService.findAll()
                : policyService.findByStatus(status);
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        return policyService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id, @Valid @RequestBody Policy policy) {
        if (policyService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        policy.setId(id);
        Policy updatedPolicy = policyService.save(policy);
        return ResponseEntity.ok(updatedPolicy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        if (policyService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        policyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}