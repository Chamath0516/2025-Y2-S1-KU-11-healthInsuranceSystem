package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.model.Dependent;
import com.hims.healthinsurancesystem.model.InsurancePlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralized plan eligibility validation.
 * Implements constraints per plan by name contains checks.
 */
@Service
public class PlanEligibilityService {

    @Autowired
    private DependentService dependentService;

    public ValidationResult validate(Customer customer, InsurancePlan plan) {
        List<String> reasons = new ArrayList<>();

        if (customer == null) {
            reasons.add("Customer not found");
            return ValidationResult.failed(reasons);
        }
        if (plan == null) {
            reasons.add("Insurance plan not found");
            return ValidationResult.failed(reasons);
        }

        // Compute age if DOB available
        Integer age = null;
        if (customer.getDateOfBirth() != null) {
            age = Period.between(customer.getDateOfBirth(), LocalDate.now()).getYears();
        }
        String gender = customer.getGender() != null ? customer.getGender().trim().toLowerCase() : null;

        String name = plan.getName() != null ? plan.getName().trim().toLowerCase() : "";

        // Senior Citizen Health Insurance -> age >= 60
        if (name.contains("senior") && name.contains("health") && name.contains("insurance")) {
            if (age == null) {
                reasons.add("Date of birth is required to apply for Senior Citizen Health Insurance");
            } else if (age < 60) {
                reasons.add("Customer must be at least 60 years old for Senior Citizen Health Insurance");
            }
        }

        // Maternity and Newborn Plans -> typically women aged 18-45
        if (name.contains("maternity")) {
            if (age == null) {
                reasons.add("Date of birth is required to apply for Maternity and Newborn Plans");
            } else if (age < 18 || age > 45) {
                reasons.add("Eligible age range for Maternity and Newborn Plans is 18-45");
            }
            if (gender == null || !gender.startsWith("f")) {
                reasons.add("Maternity plan is available for female customers only");
            }
        }

        // Family Health Insurance -> require at least 1 dependent
        if (name.contains("family") && name.contains("insurance")) {
            List<Dependent> deps = dependentService.findByCustomer(customer);
            if (deps == null || deps.isEmpty()) {
                reasons.add("Family Health Insurance requires at least one dependent on the account");
            }
        }

        // Group Health Insurance -> not directly available to individuals
        if (name.contains("group") && name.contains("health")) {
            reasons.add("Group Health Insurance is available through organizations. Please contact an insurance agent.");
        }

        // Individual Health Insurance -> min age 18
        if (name.contains("individual") && name.contains("insurance")) {
            if (age == null) {
                reasons.add("Date of birth is required to apply for Individual Health Insurance");
            } else if (age < 18) {
                reasons.add("Minimum age to apply for Individual Health Insurance is 18");
            }
        }

        // Critical Illness Insurance -> recommend 18+
        if (name.contains("critical") && name.contains("illness")) {
            if (age == null) {
                reasons.add("Date of birth is required to apply for Critical Illness Insurance");
            } else if (age < 18) {
                reasons.add("Minimum age to apply for Critical Illness Insurance is 18");
            }
        }

        return reasons.isEmpty() ? ValidationResult.ok() : ValidationResult.failed(reasons);
    }

    public static class ValidationResult {
        private final boolean eligible;
        private final List<String> reasons;

        private ValidationResult(boolean eligible, List<String> reasons) {
            this.eligible = eligible;
            this.reasons = reasons == null ? List.of() : List.copyOf(reasons);
        }

        public static ValidationResult ok() {
            return new ValidationResult(true, List.of());
        }

        public static ValidationResult failed(List<String> reasons) {
            return new ValidationResult(false, reasons);
        }

        public boolean isEligible() {
            return eligible;
        }

        public List<String> getReasons() {
            return reasons;
        }
    }
}
