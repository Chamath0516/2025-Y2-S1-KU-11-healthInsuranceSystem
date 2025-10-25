package com.hims.healthinsurancesystem.config;

import com.hims.healthinsurancesystem.model.InsurancePlan;
import com.hims.healthinsurancesystem.model.Role;
import com.hims.healthinsurancesystem.model.User;
import com.hims.healthinsurancesystem.repository.InsurancePlanRepository;
import com.hims.healthinsurancesystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InsurancePlanRepository insurancePlanRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if users already exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            createInternalUsers();
        }
        // Seed core insurance plans if missing
        seedInsurancePlans();
    }

    private void createInternalUsers() {
        System.out.println("Creating internal users with correct password hashes...");
        
        // Create Admin User
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setContactNumber(null);
        admin.setUserType("User");
        userRepository.save(admin);
        System.out.println("Admin user created with password hash: " + admin.getPassword());

        // Create Finance Manager
        User finance1 = new User();
        finance1.setUsername("finance1");
        finance1.setPassword(passwordEncoder.encode("123456"));
        finance1.setEmail("finance1@healthsure.com");
        finance1.setRole(Role.FINANCE_MANAGER);
        finance1.setFirstName("Finance");
        finance1.setLastName("Manager");
        finance1.setContactNumber(null);
        finance1.setUserType("FinanceManager");
        userRepository.save(finance1);
        System.out.println("Finance user created with password hash: " + finance1.getPassword());

        // Create Customer Support Officer
        User support1 = new User();
        support1.setUsername("support1");
        support1.setPassword(passwordEncoder.encode("123456"));
        support1.setEmail("support1@healthsure.com");
        support1.setRole(Role.CUSTOMER_SUPPORT_OFFICER);
        support1.setFirstName("Support");
        support1.setLastName("Officer");
        support1.setContactNumber(null);
        support1.setUserType("CustomerSupportOfficer");
        userRepository.save(support1);

        // Create Claim Processing Officer
        User claim1 = new User();
        claim1.setUsername("claim1");
        claim1.setPassword(passwordEncoder.encode("123456"));
        claim1.setEmail("claim1@healthsure.com");
        claim1.setRole(Role.CLAIM_PROCESSING_OFFICER);
        claim1.setFirstName("Claim");
        claim1.setLastName("Officer");
        claim1.setContactNumber(null);
        claim1.setUserType("ClaimProcessingOfficer");
        userRepository.save(claim1);

        // Create Insurance Agent
        User insurance1 = new User();
        insurance1.setUsername("insurance1");
        insurance1.setPassword(passwordEncoder.encode("123456"));
        insurance1.setEmail("insurance1@healthsure.com");
        insurance1.setRole(Role.INSURANCE_AGENT);
        insurance1.setFirstName("Insurance");
        insurance1.setLastName("Agent");
        insurance1.setContactNumber(null);
        insurance1.setUserType("InsuranceAgent");
        userRepository.save(insurance1);

        // Create Healthcare Provider
        User healthcare1 = new User();
        healthcare1.setUsername("healthcare1");
        healthcare1.setPassword(passwordEncoder.encode("123456"));
        healthcare1.setEmail("healthcare1@healthsure.com");
        healthcare1.setRole(Role.HEALTHCARE_PROVIDER);
        healthcare1.setFirstName("Healthcare");
        healthcare1.setLastName("Provider");
        healthcare1.setContactNumber(null);
        healthcare1.setUserType("HealthcareProvider");
        userRepository.save(healthcare1);

        System.out.println("Internal users created successfully!");
    }

    private void seedInsurancePlans() {
        createPlanIfMissing("Critical Illness Insurance", "Provides a lump sum payout on diagnosis of covered critical illnesses.",
                new BigDecimal("1000000"), new BigDecimal("11000"));
        createPlanIfMissing("Maternity and Newborn Plans", "Covers maternity-related medical expenses and newborn care.",
                new BigDecimal("500000"), new BigDecimal("6000"));
        createPlanIfMissing("Group Health Insurance", "Comprehensive health coverage tailored for organizations and groups.",
                new BigDecimal("2000000"), new BigDecimal("18000"));
        createPlanIfMissing("Senior Citizen Health Insurance", "Health plan designed for seniors with age-specific benefits.",
                new BigDecimal("800000"), new BigDecimal("9000"));
        createPlanIfMissing("Family Health Insurance", "Single plan that covers the entire family under one sum insured.",
                new BigDecimal("1500000"), new BigDecimal("15000"));
        createPlanIfMissing("Individual Health Insurance", "Personalized coverage for individuals with flexible options.",
                new BigDecimal("700000"), new BigDecimal("8000"));
    }

    private void createPlanIfMissing(String name, String description, BigDecimal coverage, BigDecimal premium) {
        if (insurancePlanRepository.findByName(name).isEmpty()) {
            InsurancePlan plan = new InsurancePlan();
            plan.setName(name);
            plan.setDescription(description);
            plan.setCoverageAmount(coverage);
            plan.setPremium(premium);
            insurancePlanRepository.save(plan);
            System.out.println("Seeded plan: " + name);
        }
    }
}
